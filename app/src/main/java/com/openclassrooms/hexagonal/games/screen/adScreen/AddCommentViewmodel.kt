package com.openclassrooms.hexagonal.games.screen.adScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.data.service.firebase.FirebaseAuthService
import com.openclassrooms.hexagonal.games.domain.useCase.comment.AddCommentToPostUseCase
import com.openclassrooms.hexagonal.games.domain.useCase.user.GetNameUserByMailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel responsible for managing the logic related to adding a comment to a post.
 *
 * This ViewModel handles fetching the current user's email, retrieving their name using the email,
 * and calling the use case to add the comment to a post. It also manages the UI state such as loading
 * and error states.
 *
 * @param addCommentToPostUseCase The use case that handles adding a comment to a post.
 * @param firebaseAuthService The service responsible for handling Firebase authentication.
 * @param getNameUserByMailUseCase The use case responsible for fetching the user's name using their email.
 */
@HiltViewModel
class AddCommentViewmodel @Inject constructor(
    private val addCommentToPostUseCase: AddCommentToPostUseCase,
    firebaseAuthService: FirebaseAuthService,
    private val getNameUserByMailUseCase: GetNameUserByMailUseCase
) :
    ViewModel() {

    // Mutable state representing the current UI state (loading, error, etc.)
    private val _uiState = MutableStateFlow(AddCommentUiState())

    // Publicly accessible UI state that can be observed by the UI layer
    val uiState: StateFlow<AddCommentUiState> = _uiState

    /**
     * Email of the currently authenticated user.
     * Used to fetch the user's name for the comment.
     */
    private val currentUserMail = firebaseAuthService.getCurrentUser()?.email

    /**
     * Initializes the ViewModel and logs the current user's email if available.
     */
    init {
        // Log après la récupération du currentUser
        currentUserMail?.let {
            Log.d(
                "AddCommentViewModel",
                "Utilisateur connecté : $it"
            )
        } ?: Log.d("AddCommentViewModel", "Aucun utilisateur connecté")
    }


    /**
     * Adds a comment to a post. The method retrieves the user's name using their email and then
     * calls the use case to add the comment to the post.
     *
     * @param postId The ID of the post to which the comment will be added.
     * @param commentText The content of the comment to be added.
     */
    suspend fun addComment(postId: String, commentText: String) {
        viewModelScope.launch {
            Log.d("AddCommentViewModel", "Début de l'ajout du commentaire pour le post $postId")
            _uiState.update { it.copy(loading = true, error = null) }

            // Fetch the user's name using the email
            val authorName =
                getNameUserByMailUseCase(currentUserMail ?: "") // Use email to fetch name

            if (authorName.isSuccess) {
                val name = authorName.getOrNull() ?: "Utilisateur inconnu"
                try {
                    addCommentToPostUseCase.invoke(postId, name, commentText)
                    Log.d(
                        "AddCommentViewModel",
                        "Commentaire ajouté avec succès par $name : $commentText"
                    )
                } catch (e: Exception) {
                    Log.e("AddCommentViewModel", "Erreur lors de l'ajout du commentaire", e)
                    _uiState.update {
                        it.copy(loading = false, error = R.string.error_generic)
                    }
                } catch (_: IOException) {
                    _uiState.update {
                        it.copy(loading = false, error = R.string.no_network)
                    }
                }
            } else {
                _uiState.update {
                    it.copy(loading = false, error = R.string.error_user_not_found)
                }
                Log.e("AddCommentViewModel", "User not found for email: $currentUserMail")
            }
        }
    }
}


