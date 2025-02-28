package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class CollectionUserFirebaseApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /**
     * Ajoute ou met à jour un utilisateur dans Firestore
     */
    suspend fun addUser(userId: String, userData: Map<String, Any>) {
        try {
            usersCollection.document(userId).set(userData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Récupère les informations d'un utilisateur depuis Firestore
     */
    suspend fun getUser(userId: String): DocumentSnapshot? {
        return try {
            usersCollection.document(userId).get().await()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Supprime un utilisateur de Firestore
     */
    suspend fun deleteUser(userId: String) {
        try {
            usersCollection.document(userId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getUserByEmail(email: String): Result<QuerySnapshot> {
        return try {
            val result = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            Result.success(result)
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur lors de la récupération de l'utilisateur par email: ${e.message}")
            Result.failure(e)
        }
    }
}
