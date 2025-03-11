package com.openclassrooms.hexagonal.games.screen.ad

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.service.firebase.FirebaseAuthService
import com.openclassrooms.hexagonal.games.domain.UseCase.AddPostUseCase
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.utils.image.Base64Converter
import com.openclassrooms.hexagonal.games.utils.image.BitmapConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    private val addPostUseCase: AddPostUseCase,
    private val firebaseAuthService: FirebaseAuthService
) : ViewModel() {

    /**
     * Internal mutable state flow representing the current post being edited.
     */
    private var _post = MutableStateFlow(
        Post(
            id = UUID.randomUUID().toString(),
            title = "",
            description = "",
            photoUrl = null,
            timestamp = System.currentTimeMillis(),
            author = null
        )
    )

    /**
     * Public state flow representing the current post being edited.
     * This is immutable for consumers.
     */
    val post: StateFlow<Post>
        get() = _post

    /**
     * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
     */
    val error = post.map {
        verifyPost()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    /**
     * Handles form events like title and description changes.
     *
     * @param formEvent The form event to be processed.
     */
    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.DescriptionChanged -> {
                _post.value = _post.value.copy(
                    description = formEvent.description
                )
            }

            is FormEvent.TitleChanged -> {
                _post.value = _post.value.copy(
                    title = formEvent.title
                )
            }
        }
    }

    /**
     * Adds a new post with a title, optional description, and an optional image.
     *
     * @param title The title of the post.
     * @param description The description of the post (optional).
     * @param imageBitmap The image associated with the post as a Bitmap (optional).
     */
    fun addPost(title: String, description: String?, imageBitmap: Bitmap?) {
        val currentUser = firebaseAuthService.getCurrentUser()

        if (currentUser != null) {
            val postToSave = _post.value.copy(
                author = User(
                    id = currentUser.uid,
                    name = currentUser.displayName ?: "Utilisateur inconnu"
                )
            )

            viewModelScope.launch {
                try {
                    // Log whether an image was received
                    if (imageBitmap != null) {
                        Log.d("PostViewModel", "Image reçue pour conversion.")
                    } else {
                        Log.d("PostViewModel", "Aucune image reçue.")
                    }

                    // Convert the image to a ByteArray
                    val byteArray: ByteArray? = imageBitmap?.let {
                        val byteArrayConverted =
                            BitmapConverter.toByteArray(it, Bitmap.CompressFormat.PNG, 100)
                        Log.d(
                            "PostViewModel",
                            "ByteArray converti : ${byteArrayConverted.size} octets"
                        )
                        byteArrayConverted
                    }

                    // Encode the ByteArray to a Base64 string
                    val uploadedImageUrl: String? = byteArray?.let {
                        val base64String = Base64Converter.toBase64(it)
                        Log.d(
                            "PostViewModel",
                            "Image encodée en Base64 : ${base64String.take(50)}..."
                        )
                        base64String
                    }

                    Log.d("PostViewModel", "Image finale à stocker : $uploadedImageUrl")

                    // Call the use case to add the post
                    addPostUseCase.invoke(
                        title = postToSave.title,
                        description = postToSave.description,
                        imageUri = uploadedImageUrl, // Base64 string
                        authorId = postToSave.author!!.id,
                        onSuccess = { Log.d("PostViewModel", "Post ajouté avec succès") },
                        onFailure = { error ->
                            Log.e(
                                "PostViewModel",
                                "Erreur lors de l'ajout du post",
                                error
                            )
                        }
                    )
                } catch (e: Exception) {
                    Log.e("PostViewModel", "Erreur lors du processus d'ajout du post", e)
                }
            }
        } else {
            Log.e("PostViewModel", "Aucun utilisateur connecté")
        }
    }


    /**
     * Verifies mandatory fields of the post
     * and returns a corresponding FormError if so.
     *
     * @return A FormError.TitleError if title is empty, null otherwise.
     */
    private fun verifyPost(): FormError? {
        return if (_post.value.title.isEmpty()) {
            FormError.TitleError
        } else {
            null
        }
    }

}
