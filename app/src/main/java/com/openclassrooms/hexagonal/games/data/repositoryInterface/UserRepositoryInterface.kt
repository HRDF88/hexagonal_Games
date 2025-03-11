package com.openclassrooms.hexagonal.games.data.repositoryInterface

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

/**
 * Interface defining the contract for managing user-related operations.
 */
interface UserRepositoryInterface {

    /**
     * Creates a new user in the repository.
     *
     * @param userId The unique identifier of the user.
     * @param userData A map containing the user's data fields.
     * @return A [Result] indicating success or failure.
     */
    suspend fun createUser(userId: String, userData: Map<String, Any>): Result<Unit>

    /**
     * Retrieves user data from the repository.
     *
     * @param userId The unique identifier of the user.
     * @return A [Result] containing a [DocumentSnapshot] if successful, or an error otherwise.
     */
    suspend fun getUser(userId: String): Result<DocumentSnapshot?>

    /**
     * Deletes a user from the repository.
     *
     * @param userId The unique identifier of the user.
     * @return A [Result] indicating success or failure.
     */
    suspend fun deleteUser(userId: String): Result<Unit>

    /**
     * Retrieves user data by email.
     *
     * @param email The email address of the user.
     * @return A [Result] containing a [QuerySnapshot] if successful, or an error otherwise.
     */
    suspend fun getUserByEmail(email: String): Result<QuerySnapshot>

    /**
     * Retrieves the name of a user by their email address.
     *
     * This function fetches the user’s name based on their email. It returns the name as a [String],
     * or null if the user is not found.
     *
     * @param email The email address of the user.
     * @return The name of the user if found, or null if not found.
     */
    suspend fun getNameUserByMail(email: String): String?
}