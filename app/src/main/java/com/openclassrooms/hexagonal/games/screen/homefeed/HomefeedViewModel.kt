package com.openclassrooms.hexagonal.games.screen.homefeed

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.useCase.GetPostsUseCase
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.utils.image.Base64Converter
import com.openclassrooms.hexagonal.games.utils.image.BitmapConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing data and events related to the Homefeed.
 * This ViewModel retrieves posts from the PostRepository and exposes them as a Flow<List<Post>>,
 * allowing UI components to observe and react to changes in the posts data.
 */
@HiltViewModel
class HomefeedViewModel @Inject constructor(private val getPostsUseCase: GetPostsUseCase) :
    ViewModel() {

    /**
     * StateFlow holding the list of posts.
     */
    private val _posts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    /**
     * StateFlow holding a map of post IDs to their respective Bitmap images.
     */
    private val _postImages: MutableStateFlow<Map<String, Bitmap>> = MutableStateFlow(emptyMap())
    val postImages: StateFlow<Map<String, Bitmap>> get() = _postImages

    init {
        viewModelScope.launch {
            getPostsUseCase.invoke().collect { posts ->
                _posts.value = posts
                // Convert Base64 images after receiving posts
                val images = posts.associate { post ->
                    post.id to convertBase64ToBitmap(post.photoUrl)
                }
                _postImages.value = images as Map<String, Bitmap>
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
