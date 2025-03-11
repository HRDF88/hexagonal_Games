package com.openclassrooms.hexagonal.games.domain.useCase.comment

import com.openclassrooms.hexagonal.games.data.repositoryInterface.CommentRepositoryInterface
import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving comments for a specific post.
 *
 * This class interacts with the [CommentRepositoryInterface] to fetch the comments for a given post.
 * It provides a single method to invoke the process of retrieving the comments.
 *
 * @param repository The [CommentRepositoryInterface] instance used to fetch the comments from the repository.
 */
class GetCommentsForPostUseCase @Inject constructor(
    private val repository: CommentRepositoryInterface
) {

    /**
     * Retrieves the comments for the specified post.
     *
     * This method invokes the repository's [getCommentsForPost] function to retrieve the comments associated
     * with the provided post ID.
     *
     * @param postId The unique identifier of the post whose comments will be retrieved.
     * @return A [Flow] emitting a list of [Comment] objects for the specified post.
     */
    suspend operator fun invoke(postId: String): Flow<List<Comment>> {
        return repository.getCommentsForPost(postId)
    }
}
