package com.openclassrooms.hexagonal.games.data.service.serviceInterface

import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with comment data in the application.
 * Provides methods for retrieving and adding comments to a post.
 */
interface CommentApi {

    /**
     * Retrieves the list of comments associated with a specific post.
     *
     * This method retrieves the comments for a given post identified by its [postId].
     * It returns a [Flow] that emits the list of [Comment] objects. The [Flow] will continuously
     * provide updates if the comments change (e.g., new comments are added).
     *
     * @param postId The unique identifier of the post for which comments are being retrieved.
     * @return A [Flow] emitting a list of [Comment] objects related to the specified post.
     */
    suspend fun getCommentsForPost(postId: String): Flow<List<Comment>>

    /**
     * Adds a comment to a specific post.
     *
     * This method allows a user to add a new comment to a post. The comment is identified by
     * the post ID, and includes the author's name and the text of the comment.
     *
     * @param postId The unique identifier of the post to which the comment is being added.
     * @param author The name of the author of the comment.
     * @param commentText The content of the comment being added to the post.
     */
    suspend fun addCommentToPost(postId: String, author: String, commentText: String)
}