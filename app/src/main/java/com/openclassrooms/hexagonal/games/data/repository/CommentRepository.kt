package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.repositoryInterface.CommentRepositoryInterface
import com.openclassrooms.hexagonal.games.data.service.serviceInterface.CommentApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for managing comment-related operations.
 *
 * This repository acts as an intermediary between the data layer (API) and the domain layer
 * by calling the API methods to retrieve or add comments related to posts. It implements
 * the [CommentRepositoryInterface] interface, providing the necessary operations for
 * interacting with the comment data.
 *
 * @param commentApi The API service used for retrieving and adding comments.
 */
@Singleton
class CommentRepository @Inject constructor(private val commentApi: CommentApi) :
    CommentRepositoryInterface {

    /**
     * Retrieves a list of comments for a specific post.
     *
     * This function calls the [CommentApi] to fetch the comments associated with the given post ID.
     * It returns a [Flow] that emits the list of comments for the specified post.
     *
     * @param postId The unique identifier of the post for which to retrieve comments.
     * @return A [Flow] emitting a list of [Comment] objects for the specified post.
     */
    override suspend fun getCommentsForPost(postId: String): Flow<List<Comment>> {
        return commentApi.getCommentsForPost(postId)
    }

    /**
     * Adds a comment to a post.
     *
     * This function calls the [CommentApi] to add a comment to the specified post. The comment will
     * be associated with the provided author and text.
     *
     * @param postId The unique identifier of the post to which the comment will be added.
     * @param author The author of the comment.
     * @param commentText The content of the comment.
     */
    override suspend fun addCommentToPost(postId: String, author: String, commentText: String) {
        commentApi.addCommentToPost(postId, author, commentText)
    }


}
