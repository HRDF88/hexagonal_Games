package com.openclassrooms.hexagonal.games.data.service.firebase

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.hexagonal.games.data.service.serviceInterface.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

/**
 * Implementation of [PostApi] that handles interaction with Firebase Firestore and Firebase Storage.
 */
class CollectionPostFireBaseApi : PostApi {

    val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private val firestore = FirebaseFirestore.getInstance()

    private val postCollection = firestore.collection("post")

    /**
     * Retrieves the list of posts from Firebase Firestore, ordered by timestamp in descending order.
     * The posts are enriched with author information by fetching the user data in the background.
     *
     * @return A [Flow] emitting a list of [Post] objects.
     */
    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val listener = postCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)  // Close in case of error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { document ->
                        val post = document.toObject(Post::class.java)

                        val authorRef = post?.authorRef
                        if (authorRef != null) {
                            // Fetch author in the background using the reference
                            User.fromDocumentReference(authorRef) { author ->
                                post.author = author  // Update the post with the author
                            }
                        }

                        post
                    }

                    trySend(posts)  // Send the list of posts
                }
            }

        awaitClose { listener.remove() }  // Remove listener when Flow is cancelled
    }


    /**
     * Adds a new post to Firebase Firestore. The post can include an image, which will be uploaded to Firebase Storage.
     * If an image is provided, its URL will be stored with the post data.
     *
     * @param title The title of the post.
     * @param description The description of the post (nullable).
     * @param imageUri The URI of the image to be uploaded (nullable).
     * @param authorId The ID of the author of the post.
     * @param onSuccess A callback that will be triggered upon successful post creation.
     * @param onFailure A callback that will be triggered if an error occurs during the post creation.
     */
    override suspend fun addPost(
        title: String,
        description: String?,
        imageUri: Uri?,
        authorId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postId = UUID.randomUUID().toString() // Generate a unique post ID

        // Get a reference to the author from the author ID
        val authorRef = firestore.collection("users").document(authorId)

        if (imageUri != null) {
            // Create a unique storage path for the image
            val imageRef = storageRef.child("post_images/$postId.jpg")

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL of the image
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val post = hashMapOf(
                            "id" to postId,
                            "title" to title,
                            "description" to description,
                            "photoUrl" to uri.toString(),
                            "timestamp" to System.currentTimeMillis(),
                            "authorRef" to authorRef // Use the reference of the author
                        )

                        // Add the post to Firestore
                        postCollection.document(postId)
                            .set(post)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onFailure(e) }
                    }
                }
                .addOnFailureListener { e -> onFailure(e) }
        } else {
            // If no image, create the post without the photoUrl
            val post = hashMapOf(
                "id" to postId,
                "title" to title,
                "description" to description,
                "photoUrl" to null,
                "timestamp" to System.currentTimeMillis(),
                "authorRef" to authorRef
            )

            postCollection.document(postId)
                .set(post)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }
}
