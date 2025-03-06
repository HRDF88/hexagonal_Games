package com.openclassrooms.hexagonal.games.data.service.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

/**
 * API implementation for interacting with the Firebase Firestore database for user operations.
 * Provides methods for adding, retrieving, updating, and deleting users.
 */
class CollectionUserFirebaseApi {

    private val firestore = FirebaseFirestore.getInstance()
    val usersCollection = firestore.collection("users")

    /**
     * Adds or updates a user in Firestore.
     *
     * This method will either create a new document or update an existing one
     * based on the provided user ID.
     *
     * @param userId The unique identifier of the user.
     * @param userData A map containing the user data to be saved or updated.
     */
    suspend fun addUser(userId: String, userData: Map<String, Any>) {
        try {
            usersCollection.document(userId).set(userData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Retrieves user information from Firestore.
     *
     * @param userId The unique identifier of the user.
     * @return The [DocumentSnapshot] containing the user's data, or null if the user is not found.
     */
    suspend fun getUser(userId: String): DocumentSnapshot? {
        return try {
            usersCollection.document(userId).get().await()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Deletes a user from Firestore.
     *
     * @param userId The unique identifier of the user to be deleted.
     */
    suspend fun deleteUser(userId: String) {
        try {
            usersCollection.document(userId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user to search for.
     * @return A [Result] containing a [QuerySnapshot] with the user(s) found, or a failure if an error occurs.
     */
    suspend fun getUserByEmail(email: String): Result<QuerySnapshot> {
        return try {
            val result = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            Result.success(result)
        } catch (e: Exception) {
            Log.e(
                "Firestore",
                "Erreur lors de la récupération de l'utilisateur par email: ${e.message}"
            )
            Result.failure(e)
        }
    }
}
