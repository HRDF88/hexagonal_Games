package com.openclassrooms.hexagonal.games.screen.detail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.UseCase.post.GetPostByIdUseCase
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.utils.image.Base64Converter
import com.openclassrooms.hexagonal.games.utils.image.BitmapConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPostViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    /**
     * StateFlow holding a map of post IDs to their respective Bitmap images.
     */
    private val _postImages: MutableStateFlow<Map<String, Bitmap>> = MutableStateFlow(emptyMap())
    val postImages: StateFlow<Map<String, Bitmap>> get() = _postImages

    suspend fun fetchPostById(postId: String) {
        viewModelScope.launch {
            getPostByIdUseCase(postId).collect { fetchedPost ->
                _post.value = fetchedPost
                // Si le post a une photo URL, on essaie de la convertir en Bitmap
                fetchedPost?.photoUrl?.let { photoUrl ->
                    val bitmap = convertBase64ToBitmap(photoUrl)
                    // Mettre à jour les images dans le StateFlow
                    if (bitmap != null) {
                        _postImages.value = mapOf(fetchedPost.id to bitmap)
                    }
                }
            }
        }
    }

    /**
     * Converts a Base64-encoded string into a Bitmap using the appropriate converters.
     *
     * @param base64String The Base64-encoded string representing an image.
     * @return The decoded Bitmap, or null if conversion fails.
     */
    private fun convertBase64ToBitmap(base64String: String?): Bitmap? {
        if (base64String.isNullOrEmpty()) return null
        return try {
            val byteArray = Base64Converter.fromBase64(base64String)
            BitmapConverter.fromByteArray(byteArray)
        } catch (e: Exception) {
            Log.e("HomefeedViewModel", "Error converting Base64 to Bitmap", e)
            null
        }
    }
}
