package com.openclassrooms.hexagonal.games.screen.userAccountManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.data.service.firebase.FirebaseAuthService
import com.openclassrooms.hexagonal.games.domain.useCase.user.DeleteUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that manages the user's account, including actions like logging out and deleting the user account.
 * This ViewModel interacts with use cases and services related to user authentication and data management.
 *
 * The ViewModel exposes a UI state (`uiState`) that reflects the current status of user actions like logging out
 * and deleting an account. This UI state is updated based on the outcome of those actions.
 *
 * It uses the following services and use cases:
 * - `firebaseAuthService`: Provides Firebase authentication-related functionality like signing out and deleting the user.
 * - `deleteUserUseCase`: A use case for deleting the user’s data from Firestore.
 */
@HiltViewModel
class UserAccountManagementViewModel @Inject constructor(
    private val deleteUserUseCase: DeleteUserUseCase,
    private val firebaseAuthService: FirebaseAuthService
) : ViewModel() {

    // Mutable state representing the current UI state (loading, error, etc.)
    private val _uiState = MutableStateFlow(UserAccountManagementUiState())

    // Publicly accessible UI state that can be observed by the UI layer
    val uiState: StateFlow<UserAccountManagementUiState> = _uiState

    /**
     * Logs out the current user by calling `signOut` on FirebaseAuthService and updates the UI state accordingly.
     * This method resets the `state` in the UI to indicate the user has logged out.
     */
    fun logout() {
        firebaseAuthService.signOut()
        _uiState.update { it.copy(state = false) } // Réinitialiser l'état après déconnexion
    }

    /**
     * Deletes the current user's account by performing the following steps:
     * - Deletes the user's data from Firestore.
     * - Deletes the user's account from Firebase Authentication.
     * - Signs out the user after account deletion.
     *
     * The UI state is updated to reflect the loading state during the process, as well as success or failure.
     * In case of an error, an appropriate error message is set.
     */
    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            val user = firebaseAuthService.getCurrentUser()
            if (user != null) {
                try {
                    // Attempt to delete user data from Firestore
                    deleteUserUseCase(user.uid)

                    // Delete the user's account from FirebaseAuth
                    firebaseAuthService.deleteUser(user)

                    // Sign out the user after account deletion
                    firebaseAuthService.signOut()

                    // Update the UI state to reflect the success
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

}
