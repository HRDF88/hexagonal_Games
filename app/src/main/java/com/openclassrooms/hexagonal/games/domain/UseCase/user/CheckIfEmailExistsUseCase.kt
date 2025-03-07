package com.openclassrooms.hexagonal.games.domain.`Nouveau dossier`.user

import android.util.Log
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface

/**
 * This class encapsulates the use case for checking if an email already exists in the user data source.
 * It interacts with the UserRepositoryInterface to check if a user with the provided email exists.
 *
 * The class defines an operator function `invoke`, which can be invoked like a function to check the
 * existence of the email. It fetches the result from the repository and returns a `Result` containing
 * a boolean indicating whether the email exists.
 *
 * This use case is typically used in the ViewModel to verify if a user with a given email already exists
 * in the system before performing actions like user registration or login.
 */
class CheckIfEmailExistsUseCase(private val userRepositoryInterface: UserRepositoryInterface) {

    /**
     * Checks if the provided email already exists in the user data source.
     *
     * This method calls the repository’s `getUserByEmail` function to check whether the email exists
     * in the system. It returns a `Result<Boolean>`, where `true` indicates that the email exists,
     * and `false` indicates that it does not.
     *
     * @param email The email address to be checked for existence.
     * @return A `Result<Boolean>` indicating whether the email exists in the system.
     *         A `true` value indicates that the email exists, and a `false` value indicates that it does not.
     *         If an exception occurs, the result will be a failure containing the exception.
     */
    suspend operator fun invoke(email: String): Result<Boolean> {
        return try {
            val result = userRepositoryInterface.getUserByEmail(email)

            result.map { querySnapshot ->
                !querySnapshot.isEmpty
            }
        } catch (e: Exception) {
            Log.e("CheckIfEmailExistsUseCase", "Erreur: ${e.message}")
            Result.failure(e)
        }
    }
}
