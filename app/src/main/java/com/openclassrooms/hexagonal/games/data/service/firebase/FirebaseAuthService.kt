package com.openclassrooms.hexagonal.games.data.service.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Service class responsible for handling Firebase Authentication operations.
 * Provides methods to create, authenticate, sign out, and manage users.
 */
class FirebaseAuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Creates a new user with email and password.
     *
     * @param email The email address of the user to be created.
     * @param password The password of the user.
     * @return The created [FirebaseUser] object if successful, or null if an error occurs.
     * @throws FirebaseAuthException If an authentication-related error occurs.
     * @throws Exception If a network or unexpected error occurs during user creation.
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: FirebaseAuthException) {
            // Handle Firebase-specific authentication errors
            throw FirebaseAuthException(
                "ERROR_AUTH_FAILED",
                "Erreur d'authentification: ${e.message}"
            )
        } catch (e: Exception) {
            // Handle network or other errors
            throw Exception("Erreur lors de la création de l'utilisateur: ${e.message}")
        }
    }


    /**
     * Signs in a user with email and password.
     *
     * @param email The email address of the user to sign in.
     * @param password The password of the user.
     * @return The signed-in [FirebaseUser] object if successful, or null if an error occurs.
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
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @return The current [FirebaseUser] object, or null if no user is signed in.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email The email address for which to send the password reset email.
     * @return A [Result] indicating success or failure, with an error message in case of failure.
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // Send the password reset email
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            // Email not associated with any Firebase account
            Result.failure(Exception("L'email n'est pas associé à un compte Firebase."))
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email already used by another account
            Result.failure(Exception("Cet email est déjà utilisé par un autre compte."))
        } catch (e: FirebaseAuthException) {
            // Generic Firebase error
            Result.failure(Exception("Une erreur s'est produite lors de l'envoi du mail de réinitialisation."))
        } catch (e: IOException) {
            // Network error
            Result.failure(Exception("Problème de connexion réseau."))
        } catch (e: Exception) {
            // Other unexpected errors
            Result.failure(Exception("Une erreur inconnue s'est produite."))
        }
    }


    /**
     * Deletes the specified Firebase user.
     *
     * @param firebaseUser The [FirebaseUser] to be deleted.
     * @throws Exception If an error occurs while deleting the user.
     */
    suspend fun deleteUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.delete()?.await()
    }
}
