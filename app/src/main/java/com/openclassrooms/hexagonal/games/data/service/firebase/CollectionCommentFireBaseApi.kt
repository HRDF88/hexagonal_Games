package com.openclassrooms.hexagonal.games.data.service.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.service.serviceInterface.CommentApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Firebase implementation of the [CommentApi] interface, providing methods for managing comments
 * on posts in Firestore.
 *
 * This class interacts with Firestore to perform operations such as retrieving comments for a
 * specific post and adding new comments to a post.
 */
class CollectionCommentFireBaseApi : CommentApi {
    private val db = FirebaseFirestore.getInstance()


    /**
     * Retrieves comments for a specific post from Firestore.
     *
     * This method listens for changes in the "comment" sub-collection of a specific post in Firestore.
     * It returns a [Flow] that emits a list of comments every time the data is updated.
     *
     * @param postId The ID of the post whose comments are being fetched.
     * @return A [Flow] emitting a list of [Comment] objects whenever the comments are updated.
     */
    override suspend fun getCommentsForPost(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = db.collection("post").document(postId).collection("comment")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close the Flow in case of an error
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(comments).isSuccess // Emit the list of comments
            }

        awaitClose { listener.remove() } // Stop listening when the Flow is cancelled
    }

    /**
     * Adds a new comment to a post in Firestore.
     *
     * This method adds a comment to the "comment" sub-collection of a specific post in Firestore.
     * It automatically generates an ID for the new comment and stores the author, comment text,
     * and timestamp in the database.
     *
     * @param postId The ID of the post to which the comment is being added.
     * @param author The author of the comment.
     * @param commentText The text content of the comment.
     */
    override suspend fun addCommentToPost(postId: String, author: String, commentText: String) {

        // Reference to the "comment" sub-collection of the specific post
        val commentRef = db.collection("post").document(postId).collection("comment")

        // Create a new comment with an auto-generated ID
        val newComment = hashMapOf(
            "author" to author,
            "comment" to commentText,
            "timestamp" to System.currentTimeMillis(),
        )

        commentRef.add(newComment)
            .addOnSuccessListener { documentReference ->
                println("Commentaire ajouté avec succès ! ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'ajout du commentaire : ${e.message}")
            }
    }

}