package com.openclassrooms.hexagonal.games.screen.adScreen

/**
 * Data class representing the UI state for adding a comment.
 *
 * This class holds the state values related to the process of adding a comment to a post.
 * It includes properties to track loading states and potential error messages.
 *
 * @param error The error message resource ID to be displayed in case of an error. Defaults to `null`.
 * @param loading A flag indicating whether the comment is being added (loading state). Defaults to `false`.
 */
data class AddCommentUiState(
    val error: Int? = null,
    val loading: Boolean = false,
)