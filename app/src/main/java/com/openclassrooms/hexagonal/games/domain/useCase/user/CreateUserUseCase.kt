package com.openclassrooms.hexagonal.games.domain.useCase.user

import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

/**
 * This class encapsulates the use case for creating a new user in the system.
 * It interacts with the UserRepositoryInterface to add a new user to the data source.
 *
 * The class defines an operator function `invoke`, which can be invoked like a function to create
 * a user by providing the user ID and user data. It returns a `Result` containing either success
 * or failure based on whether the operation succeeds.
 *
 * This use case is typically used in the ViewModel to create a new user in the system, such as during
 * user registration.
 */
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {

    /**
     * Creates a new user with the provided user ID and data.
     *
     * This method calls the repository’s `createUser` function to add a new user to the system.
     * It returns a `Result<Unit>`, where `Result.success(Unit)` indicates that the user was created
     * successfully, and `Result.failure(exception)` indicates an error occurred during the creation process.
     *
     * @param userId The unique identifier for the user to be created.
     * @param userData A map containing the data for the user to be created, such as name, email, etc.
     * @return A `Result<Unit>` indicating the success or failure of the user creation process.
     *         A `Result.success(Unit)` indicates successful creation, while `Result.failure(exception)`
     *         contains an error if the creation fails.
     */
    suspend operator fun invoke(userId: String, userData: Map<String, Any>): Result<Unit> {
        return userRepository.createUser(userId, userData)
    }
}
