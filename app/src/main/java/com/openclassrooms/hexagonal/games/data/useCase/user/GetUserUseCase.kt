package com.openclassrooms.hexagonal.games.data.useCase.user

import com.google.firebase.firestore.DocumentSnapshot
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

/**
 * This class encapsulates the use case for retrieving a user's data from the system.
 * It interacts with the UserRepositoryInterface to fetch the user information from the data source.
 *
 * The class defines an operator function `invoke`, which can be invoked like a function to retrieve
 * a user by providing the user ID. It returns a `Result` containing either the user data or an error,
 * based on whether the operation succeeds.
 *
 * This use case is typically used in the ViewModel when user information is required for display or processing.
 */
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {

    /**
     * Retrieves the user with the specified user ID.
     *
     * This method calls the repository’s `getUser` function to fetch the user data from the system.
     * It returns a `Result<DocumentSnapshot?>`, where `Result.success(DocumentSnapshot)` contains
     * the user data if found, and `Result.failure(exception)` contains an error if the user cannot
     * be retrieved.
     *
     * @param userId The unique identifier for the user whose data is to be fetched.
     * @return A `Result<DocumentSnapshot?>` containing the user data if successful, or an error if the
     *         operation fails. If the user is not found, the result will contain `null`.
     */
    suspend operator fun invoke(userId: String): Result<DocumentSnapshot?> {
        return userRepository.getUser(userId)
    }
}
