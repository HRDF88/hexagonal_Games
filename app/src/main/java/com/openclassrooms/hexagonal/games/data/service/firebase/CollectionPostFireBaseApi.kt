package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class CollectionPostFireBaseApi : PostApi {

    val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private val firestore = FirebaseFirestore.getInstance()

    private val postCollection = firestore.collection("post")

    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val listener = postCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)  // Ferme en cas d'erreur
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { document ->
                        val post = document.toObject(Post::class.java)

                        val authorRef = post?.authorRef
                        if (authorRef != null) {
                            // Utiliser le mapper pour récupérer l'auteur en arrière-plan
                            User.fromDocumentReference(authorRef) { author ->
                                post.author = author  // Mettre à jour l'objet post avec l'auteur
                            }
                        }

                        post
                    }

                    trySend(posts)  // Envoie la liste des posts
                }
            }

        awaitClose { listener.remove() }  // Ferme l'écouteur quand Flow est annulé
    }


    override suspend fun addPost(
        title: String,
        description: String?,
        imageUri: Uri?,
        authorId: String,  // authorId est un String
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postId = UUID.randomUUID().toString() // Générer un ID unique pour le post

        // Récupérer la référence de l'auteur depuis son ID
        val authorRef = firestore.collection("users").document(authorId)

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
                            "authorRef" to authorRef // Utiliser la référence de l'auteur
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
                "authorRef" to authorRef // Utiliser la référence de l'auteur
            )

            postCollection.document(postId)
                .set(post)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }
}
