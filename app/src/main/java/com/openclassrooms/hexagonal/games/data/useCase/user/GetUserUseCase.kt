package com.openclassrooms.hexagonal.games.data.useCase.user

import com.google.firebase.firestore.DocumentSnapshot
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryInterface
) {
    suspend operator fun invoke(userId: String): Result<DocumentSnapshot?> {
        return userRepository.getUser(userId)
    }
}
