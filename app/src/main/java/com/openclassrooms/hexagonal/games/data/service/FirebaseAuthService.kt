package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import java.io.IOException

class FirebaseAuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Créer un utilisateur avec email et mot de passe
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: FirebaseAuthException) {
            // Gérer les erreurs spécifiques Firebase ici
            throw FirebaseAuthException("ERROR_AUTH_FAILED", "Erreur d'authentification: ${e.message}")
        } catch (e: Exception) {
            // Gérer les autres erreurs (par exemple, erreurs réseau)
            throw Exception("Erreur lors de la création de l'utilisateur: ${e.message}")
        }
    }


    /**
     * Se connecter avec email et mot de passe
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Déconnecter l'utilisateur
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Récupérer l'utilisateur actuel
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // Envoi du mail de réinitialisation
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            // L'email n'existe pas dans Firebase
            Result.failure(Exception("L'email n'est pas associé à un compte Firebase."))
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email déjà utilisé pour un autre compte
            Result.failure(Exception("Cet email est déjà utilisé par un autre compte."))
        } catch (e: FirebaseAuthException) {
            // Erreur générique Firebase
            Result.failure(Exception("Une erreur s'est produite lors de l'envoi du mail de réinitialisation."))
        } catch (e: IOException) {
            // Erreur réseau
            Result.failure(Exception("Problème de connexion réseau."))
        } catch (e: Exception) {
            // Toute autre erreur
            Result.failure(Exception("Une erreur inconnue s'est produite."))
        }
    }


    suspend fun deleteUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.delete()?.await()
    }
}
