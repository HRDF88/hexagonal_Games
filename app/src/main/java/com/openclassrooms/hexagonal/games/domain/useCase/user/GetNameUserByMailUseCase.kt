package com.openclassrooms.hexagonal.games.domain.UseCase.user

import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

/**
 * Use case for retrieving a user's name based on their email address.
 *
 * This use case calls the repository method to fetch the user's name.
 * It handles the result, either returning the name of the user or an error if the user is not found.
 *
 * @param userRepository The repository interface used to interact with the user data source (e.g., Firestore).
 */
class GetNameUserByMailUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {

    /**
     * Retrieves the name of the user based on their email address.
     *
     * This method calls the repository's [getNameUserByMail] method to get the user's name.
     * If the name is found, it returns a success result with the user's name.
     * If no user is found or an error occurs, it returns a failure result.
     *
     * @param email The email address of the user.
     * @return A [Result] containing either the user's name if successful, or an exception if an error occurs.
     */
    suspend operator fun invoke(email: String): Result<String> {
        return try {
            // Fetch the user's name by email using the repository
            val userName = userRepository.getNameUserByMail(email)

            // If user name is found, return a success result
            if (userName != null) {
                Result.success(userName)
            } else {
                // If user name is not found, return a failure result
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            // Return failure result if any error occurs during the process
            Result.failure(e)
        }
    }
}
