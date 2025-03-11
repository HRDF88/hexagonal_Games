package com.openclassrooms.hexagonal.games.screen.detail

/**
 * Data class representing the UI state for displaying post details.
 *
 * This class holds state information related to the loading and error status
 * while retrieving or displaying the details of a post.
 *
 * @param error The error message resource ID to be displayed in case of an error. Defaults to `null`.
 * @param loading A flag indicating whether the post details are being loaded. Defaults to `false`.
 */
data class DetailPostUiState(
    val error: Int? = null,
    val loading: Boolean = false,
)
