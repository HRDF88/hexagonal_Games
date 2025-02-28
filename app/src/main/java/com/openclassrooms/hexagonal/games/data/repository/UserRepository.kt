package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import com.openclassrooms.hexagonal.games.data.service.CollectionUserFirebaseApi
import javax.inject.Inject

class UserRepository @Inject constructor(private val api: CollectionUserFirebaseApi) : UserRepositoryInterface {

    /**
     * Ajoute un utilisateur
     */
    override suspend fun createUser(userId: String, userData: Map<String, Any>): Result<Unit> {
        return try {
            api.addUser(userId, userData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Récupère les données de l'utilisateur
     */
    override suspend fun getUser(userId: String): Result<DocumentSnapshot?> {
        return try {
            val userSnapshot = api.getUser(userId)
            Result.success(userSnapshot)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Supprime un utilisateur
     */
    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            api.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByEmail(email: String): Result<QuerySnapshot> {
        return api.getUserByEmail(email)
    }
}
