package com.openclassrooms.hexagonal.games.screen.detail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.useCase.comment.GetCommentsForPostUseCase
import com.openclassrooms.hexagonal.games.domain.useCase.post.GetPostByIdUseCase
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.utils.image.Base64Converter
import com.openclassrooms.hexagonal.games.utils.image.BitmapConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailPostViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val getCommentsForPostUseCase: GetCommentsForPostUseCase,
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    /**
     * StateFlow holding a map of post IDs to their respective Bitmap images.
     */
    private val _postImages: MutableStateFlow<Map<String, Bitmap>> = MutableStateFlow(emptyMap())
    val postImages: StateFlow<Map<String, Bitmap>> get() = _postImages

    /**
     * StateFlow holding a list of comments for a given post.
     */
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    /**
     * StateFlow representing the UI state, including loading status and errors.
     */
    private val _uiState = MutableStateFlow(DetailPostUiState())

    /** Publicly accessible UI state that can be observed by the UI layer. */
    val uiState: StateFlow<DetailPostUiState> = _uiState

    /**
     * Fetches a post by its unique identifier.
     *
     * @param postId The unique ID of the post.
     */
    suspend fun fetchPostById(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                getPostByIdUseCase(postId).collect { fetchedPost ->
                    _post.value = fetchedPost
                    // If the post has a photo URL, attempt to convert it to a Bitmap
                    fetchedPost?.photoUrl?.let { photoUrl ->
                        val bitmap = convertBase64ToBitmap(photoUrl)
                        if (bitmap != null) {
                            _postImages.value = mapOf(fetchedPost.id to bitmap)
                            // Update the UI state to reflect the success
                            _uiState.update { it.copy(loading = false) }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = R.string.error_generic
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = R.string.no_network
                    )
                }
            }
        }
    }

    /**
     * Fetches comments for a specific post in real time.
     *
     * @param postId The unique ID of the post.
     */
    fun fetchComments(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                getCommentsForPostUseCase(postId).collect { commentList ->
                    _comments.value = commentList
                    _uiState.update { it.copy(loading = false) }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = R.string.error_generic
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = R.string.no_network
                    )
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
