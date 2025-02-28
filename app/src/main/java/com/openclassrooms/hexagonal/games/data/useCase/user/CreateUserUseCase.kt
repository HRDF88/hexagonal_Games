package com.openclassrooms.hexagonal.games.data.useCase.user

import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {
    suspend operator fun invoke(userId: String, userData: Map<String, Any>): Result<Unit> {
        return userRepository.createUser(userId, userData)
    }
}
