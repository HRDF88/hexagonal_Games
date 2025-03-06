package com.openclassrooms.hexagonal.games.data.useCase.user

import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

/**
 * This class encapsulates the use case for deleting a user from the system.
 * It interacts with the UserRepositoryInterface to remove a user from the data source.
 *
 * The class defines an operator function `invoke`, which can be invoked like a function to delete
 * a user by providing the user ID. It returns a `Result` containing either success or failure
 * based on whether the operation succeeds.
 *
 * This use case is typically used in the ViewModel when a user needs to be deleted from the system.
 */
class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {

    /**
     * Deletes the user with the specified user ID.
     *
     * This method calls the repository’s `deleteUser` function to remove the user from the system.
     * It returns a `Result<Unit>`, where `Result.success(Unit)` indicates that the user was
     * successfully deleted, and `Result.failure(exception)` indicates an error occurred during
     * the deletion process.
     *
     * @param userId The unique identifier for the user to be deleted.
     * @return A `Result<Unit>` indicating the success or failure of the user deletion process.
     *         A `Result.success(Unit)` indicates the user was deleted successfully, while
     *         `Result.failure(exception)` contains an error if the deletion fails.
     */
    suspend operator fun invoke(userId: String): Result<Unit> {
        return userRepository.deleteUser(userId)
    }
}
