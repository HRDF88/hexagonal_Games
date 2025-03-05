package com.openclassrooms.hexagonal.games.domain.model

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

/**
 * This class represents a User data object. It holds basic information about a user, including
 * their ID, first name, and last name. The class implements Serializable to allow for potential
 * serialization needs.
 */
data class User(
    /**
     * Unique identifier for the User.
     */
    val id: String,

    /**
     * User's first name.
     */
    val name: String,

    ) : Serializable {
    constructor() : this("", "")

    companion object {
        fun fromDocumentReference(docRef: DocumentReference, callback: (User?) -> Unit) {
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                callback(user)  // Appeler le callback une fois l'utilisateur récupéré
            }.addOnFailureListener {
                callback(null)  // Si échec, retourner null
            }
        }

        fun toDocumentReference(user: User, firestore: FirebaseFirestore): DocumentReference {
            return firestore.collection("users").document(user.id) // Créer une référence vers le document de l'utilisateur
        }

    }
}
