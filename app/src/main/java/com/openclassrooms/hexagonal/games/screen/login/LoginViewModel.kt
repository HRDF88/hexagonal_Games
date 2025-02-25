package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val emailExists: Boolean) : LoginState()
    data class Error(val message: Int) : LoginState()
}

class LoginEnteredViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

        val user = firebaseAuth.currentUser
        if (user != null) {
            // L'utilisateur est connecté, mais ce n'est pas suffisant pour confirmer la connexion
            if (user.isEmailVerified) {
                _loginState.value =
                    LoginState.Success(true) // Utilise un succès si l'utilisateur est correctement authentifié
            }
        } else {
            // L'utilisateur n'est pas connecté
            _loginState.value = LoginState.Error(R.string.error_not_authenticated)
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    suspend fun checkIfEmailExists(email: String): Boolean {
        _loginState.value = LoginState.Loading
        val db = FirebaseFirestore.getInstance()

        return try {
            val documents = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()  // Utilisation de 'await()' pour suspendre la fonction et obtenir le résultat

            if (documents.isEmpty) {
                Log.d("Firestore", "Email disponible")
                _loginState.value = LoginState.Idle
                false // Email non trouvé
            } else {
                Log.d("Firestore", "Email déjà utilisé")
                _loginState.value = LoginState.Idle
                true // Email trouvé
            }
        } catch (e: IOException) {
            Log.e("Firestore", "Erreur de connexion réseau", e)
            _loginState.value = LoginState.Idle
            throw IOException("Erreur de connexion réseau : ${e.message}") // Relance l'exception
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur Firestore", e)
            _loginState.value = LoginState.Idle
            false // En cas d'autre erreur, on considère l'email comme non trouvé
        }
    }


    // 🔹 Connexion avec mot de passe

    suspend fun signIn(email: String, password: String): Result<Unit> {
        _loginState.value = LoginState.Loading
        return try {
            // Connexion de l'utilisateur avec email et mot de passe

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            _loginState.value = LoginState.Success(true)
            _loginState.value = LoginState.Idle
            Result.success(Unit)
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(R.string.incorrect_password)
            _loginState.value = LoginState.Idle
            Result.failure(e)
        }
    }


    // 🔹 Création d’un compte
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
                "password" to password,  // Ajoute le mot de passe ici si nécessaire (en général, mieux d'éviter de stocker un mot de passe en clair)
                "createdAt" to System.currentTimeMillis()  // Timestamp pour savoir quand l'utilisateur a été créé
            )

            withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid) // Utilisation de l'UID de l'utilisateur pour créer un document unique
                    .set(userData)
                    .await()
            }
            _loginState.value = LoginState.Idle
            Result.success(Unit) // Succès
        } catch (e: IOException) {
            _loginState.value = LoginState.Idle
            Result.failure(Exception("Erreur réseau, vérifiez votre connexion 🛜❌", e))
        } catch (e: Exception) {
            _loginState.value = LoginState.Idle
            Result.failure(e) // Gestion des autres erreurs Firebase
        }
    }
}
