package com.openclassrooms.hexagonal.games.data.useCase.user

import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return userRepository.deleteUser(userId)
    }
}
