package com.openclassrooms.hexagonal.games.screen.userAccountManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.data.service.FirebaseAuthService
import com.openclassrooms.hexagonal.games.data.useCase.user.DeleteUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAccountManagementViewModel @Inject constructor(
    private val deleteUserUseCase: DeleteUserUseCase,
    private val firebaseAuthService: FirebaseAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserAccountManagementUiState())
    val uiState: StateFlow<UserAccountManagementUiState> = _uiState

    fun logout() {
       firebaseAuthService.signOut()
        _uiState.update { it.copy(state = false) } // Réinitialiser l'état après déconnexion
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            val user = firebaseAuthService.getCurrentUser()
            if (user != null) {
                try {
                    // Supprimer les données de Firestore
                    deleteUserUseCase(user.uid)

                    // Supprimer l'utilisateur de FirebaseAuth
                    firebaseAuthService.deleteUser(user)

                    // Déconnexion après suppression
                    firebaseAuthService.signOut()

                    // Mettre à jour l'état (succès)
                    _uiState.update { it.copy(loading = false, state = false) }

                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = R.string.error_find_password
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(loading = false, error = R.string.error_find_password) }
            }
        }
    }

    /**
     * Fonction pour réinitialiser l'état de l'UI après une erreur
     */
    fun resetUiState() {
        _uiState.update { UserAccountManagementUiState() }
    }
}
