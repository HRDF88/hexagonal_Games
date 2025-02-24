package com.openclassrooms.hexagonal.games.screen.loginEntered

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val emailExists: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginEnteredViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun checkIfEmailExists(email: String) {
        _loginState.value = LoginState.Loading

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val exists = !signInMethods.isNullOrEmpty()
                    _loginState.value = LoginState.Success(exists)
                } else {
                    Log.e("FirebaseAuth", "Erreur Firebase", task.exception)
                    _loginState.value = LoginState.Error("Erreur lors de la vérification")
                }
            }
    }
}
