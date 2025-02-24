package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val emailExists: Boolean) : LoginState()
    data class Error(val message: Int) : LoginState()
}

class LoginEnteredViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun checkIfEmailExists(email: String, onResult: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val fakePassword = "fakePassword123" // Mot de passe fictif

        auth.signInWithEmailAndPassword(email, fakePassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Connexion réussie, l'utilisateur existe
                    auth.signOut() // Déconnexion immédiate
                    onResult(true)
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // L'utilisateur n'existe pas
                            onResult(false)
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // L'utilisateur existe mais le mot de passe est incorrect
                            onResult(true)
                        }
                        else -> {
                            // Autre erreur
                            task.exception?.printStackTrace()
                            onResult(false)
                        }
                    }
                }
            }
    }


    // 🔹 Connexion avec mot de passe
    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Idle
                    onSuccess()
                } else {
                    _loginState.value = LoginState.Error(R.string.incorrect_password)
                }
            }
    }

    // 🔹 Création d’un compte
    fun createAccount(email: String, password: String, onSuccess: () -> Unit) {
        _loginState.value = LoginState.Loading

        // Crée un utilisateur avec l'email et le mot de passe
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Idle
                    // Ajouter un délai avant de vérifier si l'email existe
                } else {
                    _loginState.value = LoginState.Error(R.string.error_create_account)
                }
            }
    }
}
