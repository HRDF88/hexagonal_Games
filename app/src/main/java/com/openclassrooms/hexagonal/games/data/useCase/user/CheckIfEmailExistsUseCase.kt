package com.openclassrooms.hexagonal.games.data.useCase.user

import android.util.Log
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface

class CheckIfEmailExistsUseCase(private val userRepositoryInterface: UserRepositoryInterface) {

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
