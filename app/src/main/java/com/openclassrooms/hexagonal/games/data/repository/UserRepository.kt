package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import com.openclassrooms.hexagonal.games.data.service.firebase.CollectionUserFirebaseApi
import javax.inject.Inject

/**
 * Repository class responsible for managing user data using Firebase.
 * Implements [UserRepositoryInterface].
 *
 * @param api The Firebase API for user collection operations.
 */
class UserRepository @Inject constructor(private val api: CollectionUserFirebaseApi) :
    UserRepositoryInterface {

    /**
     * Adds a new user to the Firebase Firestore collection.
     *
     * @param userId The unique identifier of the user.
     * @param userData A map containing user data fields.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun createUser(userId: String, userData: Map<String, Any>): Result<Unit> {
        return try {
            api.addUser(userId, userData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves user data from Firebase Firestore.
     *
     * @param userId The unique identifier of the user.
     * @return A [Result] containing a [DocumentSnapshot] if successful, or an error otherwise.
     */
    override suspend fun getUser(userId: String): Result<DocumentSnapshot?> {
        return try {
            val userSnapshot = api.getUser(userId)
            Result.success(userSnapshot)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a user from the Firebase Firestore collection.
     *
     * @param userId The unique identifier of the user.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            api.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves user data based on email.
     *
     * @param email The email address of the user.
     * @return A [Result] containing a [QuerySnapshot] if successful, or an error otherwise.
     */
    override suspend fun getUserByEmail(email: String): Result<QuerySnapshot> {
        return api.getUserByEmail(email)
    }

    /**
     * Retrieves the name of a user based on their email.
     *
     * This method queries the repository or API to fetch the user's name by their email address.
     * If the user is found, the name is returned; otherwise, `null` is returned.
     *
     * @param email The email address of the user whose name is to be fetched.
     * @return The name of the user if found, or `null` if no user is found with the provided email.
     */
    override suspend fun getNameUserByMail(email: String): String? {
        return api.getNameUserByMail(email)
    }
}
