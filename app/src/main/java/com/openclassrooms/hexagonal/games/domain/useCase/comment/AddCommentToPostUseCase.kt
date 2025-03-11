package com.openclassrooms.hexagonal.games.domain.useCase.comment

import com.openclassrooms.hexagonal.games.data.repositoryInterface.CommentRepositoryInterface
import javax.inject.Inject

/**
 * Use case for adding a comment to a post.
 *
 * This class interacts with the [CommentRepositoryInterface] to add a new comment to a specific post.
 * It provides a single method to invoke the process of adding a comment, passing the necessary data
 * such as the post ID, author, and comment text.
 *
 * @param repository The [CommentRepositoryInterface] instance used to perform the data operation.
 */
class AddCommentToPostUseCase @Inject constructor(
    private val repository: CommentRepositoryInterface
) {

    /**
     * Adds a comment to the specified post.
     *
     * This method invokes the repository's [addCommentToPost] function to add a comment with the given data.
     *
     * @param postId The unique identifier of the post to which the comment will be added.
     * @param author The name of the author who is writing the comment.
     * @param commentText The content of the comment.
     */
    suspend operator fun invoke(postId: String, author: String, commentText: String) {
        repository.addCommentToPost(postId, author, commentText)
    }
}
