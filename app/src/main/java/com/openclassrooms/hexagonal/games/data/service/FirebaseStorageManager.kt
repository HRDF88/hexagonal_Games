package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class FirebaseStorageManager : PostApi{

    val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private val firestore = FirebaseFirestore.getInstance()

    private val postCollection = firestore.collection("post")

    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val listener = postCollection
            .orderBy("timestamp", Query.Direction.DESCENDING) // Trier du plus récent au plus ancien
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // Ferme le Flow en cas d'erreur
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { document ->
                        document.toObject(Post::class.java)
                    }
                    trySend(posts) // Envoie la liste des posts
                }
            }

        awaitClose { listener.remove() } // Supprime l'écouteur quand Flow est annulé
    }

    override suspend fun addPost(
        title: String,
        description: String?,
        imageUri: Uri?,
        authorId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postId = UUID.randomUUID().toString() // Générer un ID unique pour le post

        if (imageUri != null) {
            // Créer un chemin de stockage unique pour l'image
            val imageRef = storageRef.child("post_images/$postId.jpg")

            // Uploader l'image dans Firebase Storage
            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Récupérer l'URL de l'image
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val post = hashMapOf(
                            "id" to postId,
                            "title" to title,
                            "description" to description,
                            "photoUrl" to uri.toString(),
                            "timestamp" to System.currentTimeMillis(),
                            "author" to firestore.collection("users").document(authorId) // Référence à l'auteur
                        )

                        // Ajouter le post à Firestore
                        postCollection.document(postId)
                            .set(post)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onFailure(e) }
                    }
                }
                .addOnFailureListener { e -> onFailure(e) }
        } else {
            // Si aucune image, créer le post sans photoUrl
            val post = hashMapOf(
                "id" to postId,
                "title" to title,
                "description" to description,
                "photoUrl" to null,
                "timestamp" to System.currentTimeMillis(),
                "author" to firestore.collection("users").document(authorId)
            )

            postCollection.document(postId)
                .set(post)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }
}
