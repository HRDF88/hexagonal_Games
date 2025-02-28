package com.openclassrooms.hexagonal.games.data.repositoryInterface

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

interface UserRepositoryInterface  {

    suspend fun createUser(userId: String, userData: Map<String, Any>): Result<Unit>

    suspend fun getUser(userId: String): Result<DocumentSnapshot?>

    suspend fun deleteUser(userId: String): Result<Unit>

    suspend fun getUserByEmail(email: String): Result<QuerySnapshot>
}