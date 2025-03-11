package com.openclassrooms.hexagonal.games.data.repositoryInterface

import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the repository operations for managing comments.
 *
 * This interface is used for interacting with the data source that handles comment-related
 * operations. It provides methods to retrieve and add comments to a post.
 */
interface CommentRepositoryInterface {

    /**
     * Retrieves a list of comments for a specific post.
     *
     * This function returns a [Flow] that emits a list of [Comment] objects associated
     * with the specified post. The flow will emit the comments whenever they are updated.
     *
     * @param postId The unique identifier of the post for which comments are to be fetched.
     * @return A [Flow] emitting a list of [Comment] objects associated with the given post.
     */
    suspend fun getCommentsForPost(postId: String): Flow<List<Comment>>

    /**
     * Adds a new comment to a specific post.
     *
     * This function allows adding a comment to a post by providing the post ID, the author's
     * name, and the comment text. The comment will be saved to the data source.
     *
     * @param postId The unique identifier of the post to which the comment will be added.
     * @param author The name of the author of the comment.
     * @param commentText The text content of the comment.
     */
    suspend fun addCommentToPost(postId: String, author: String, commentText: String)


}