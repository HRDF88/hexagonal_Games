package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Sealed class representing the possible states of the login process.
 *
 * This class encapsulates the different states during the authentication process, such as:
 * - Idle: When no action is being performed.
 * - Loading: When a network operation is in progress.
 * - Success: When the operation is successful, with an optional flag indicating if the email exists.
 * - Error: When an error occurs, with a message passed as a resource ID.
 */
sealed class LoginState {

    /**
     * Represents the idle state of the login process.
     */
    data object Idle : LoginState()

    /**
     * Represents the loading state of the login process, when a network operation is ongoing.
     */
    data object Loading : LoginState()


    /**
     * Represents a successful login state, with a flag indicating whether the email already exists.
     * @param emailExists A boolean indicating if the email already exists in the system.
     */
    data class Success(val emailExists: Boolean) : LoginState()

    /**
     * Represents an error state during the login process.
     * @param message The resource ID of the error message to be displayed.
     */
    data class Error(val message: Int) : LoginState()
}

/**
 * ViewModel class for managing user login and account creation operations.
 *
 * This class interacts with Firebase Authentication and Firestore to handle user login,
 * account creation, password reset, and email validation. It exposes the current login state
 * as a `StateFlow` and updates it based on various events.
 */
class LoginEnteredViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Listener to track changes in authentication state.
     *
     * This listener listens for changes in the authentication state and updates the login state accordingly.
     * If the user is authenticated and their email is verified, a successful login state is set.
     * If the user is not authenticated, an error state is set.
     */
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

        val user = firebaseAuth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                _loginState.value =
                    LoginState.Success(true) // Successful login with verified email
            }
        } else {
            // Not authenticated
            _loginState.value = LoginState.Error(R.string.error_not_authenticated)
        }
    }

    // Add authentication state listener
    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    /**
     * Mutable state flow representing the current login state.
     */
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)

    /**
     * Publicly exposed state flow to observe the login state.
     */
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Checks if an email already exists in the Firestore database.
     *
     * This function performs a query to Firestore to check if the provided email is already registered.
     * It returns true if the email exists, false otherwise.
     *
     * @param email The email to be checked.
     * @return A boolean indicating whether the email already exists.
     */
    suspend fun checkIfEmailExists(email: String): Boolean {
        _loginState.value = LoginState.Loading
        val db = FirebaseFirestore.getInstance()

        return try {
            val documents = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            val emailExists = !documents.isEmpty

            Log.d("Firestore", if (emailExists) "Email déjà utilisé" else "Email disponible")
            _loginState.value = LoginState.Idle
            emailExists
        } catch (e: IOException) {
            Log.e("Firestore", "Erreur réseau: ${e.message}")
            _loginState.value = LoginState.Error(R.string.no_network)
            delay(2000)
            _loginState.value = LoginState.Idle
            false
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur Firestore: ${e.message}")
            _loginState.value = LoginState.Error(R.string.error_generic)
            delay(2000)
            _loginState.value = LoginState.Idle
            false
        }
    }

    /**
     * Signs the user in using email and password.
     *
     * This function attempts to sign in the user with the provided email and password.
     * If successful, it updates the login state to success, otherwise, it sets an error state.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return A result indicating success or failure.
     */
    suspend fun signIn(email: String, password: String): Result<Unit> {
        _loginState.value = LoginState.Loading
        return try {
            // Connexion de l'utilisateur avec email et mot de passe

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            _loginState.value = LoginState.Success(true)
            Result.success(Unit)
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(R.string.incorrect_password)
            Result.failure(e)
        } catch (e: IOException) {
            _loginState.value = LoginState.Error(R.string.no_network)
            Result.failure(e)
        }
    }


    /**
     * Creates a new user account with email, password, and full name.
     *
     * This function attempts to create a new user with the provided credentials. It also stores the user's
     * information in Firestore, including their email, full name, and account creation timestamp.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param fullName The user's full name.
     * @return A result indicating success or failure.
     */
    suspend fun createAccount(email: String, password: String, fullName: String): Result<Unit> {
        return try {
            _loginState.value = LoginState.Loading
            // Crée l'utilisateur dans Firebase Authentication
            val authResult = withContext(Dispatchers.IO) {
                auth.createUserWithEmailAndPassword(email, password).await()
            }

            val user = authResult.user ?: return Result.failure(Exception("Utilisateur non trouvé"))

            // Ajoute l'utilisateur à Firestore
            val userData = hashMapOf(
                "email" to email,
                "nom&prenom" to fullName,
                "createdAt" to System.currentTimeMillis()  // Timestamp pour savoir quand l'utilisateur a été créé
            )

            withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid) // Utilisation de l'UID de l'utilisateur pour créer un document unique
                    .set(userData)
                    .await()
            }
            Result.success(Unit) // Succès
        } catch (e: IOException) {
            _loginState.value = LoginState.Error(R.string.no_network)
            delay(2000)
            _loginState.value = LoginState.Idle
            Result.failure(e)
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(R.string.error_create_account)
            delay(2000)
            _loginState.value = LoginState.Idle
            Result.failure(e)
        } finally {
            delay(2000)
            _loginState.value = LoginState.Idle
        }
    }

    /**
     * Sends a password reset email to the user.
     *
     * This function sends a password reset email to the provided email address.
     * If the operation fails, it returns an error state.
     *
     * @param email The email address of the user requesting a password reset.
     * @return A result indicating success or failure.
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            _loginState.value = LoginState.Loading
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(R.string.error_find_password)
            Result.failure(e)
        } catch (e: IOException) {
            _loginState.value = LoginState.Error(R.string.no_network)
            Result.failure(e)
        } finally {
            delay(2000)
            _loginState.value = LoginState.Idle
        }
    }
}
