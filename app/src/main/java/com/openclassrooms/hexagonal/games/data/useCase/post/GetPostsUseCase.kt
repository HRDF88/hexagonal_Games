package com.openclassrooms.hexagonal.games.data.useCase.post

import com.openclassrooms.hexagonal.games.data.repositoryInterface.PostRepositoryInterface
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * This class encapsulates the use case for retrieving the list of posts from the data source.
 * It abstracts the logic of fetching posts by interacting with the PostRepositoryInterface.
 *
 * The class defines an operator function `invoke`, allowing it to be invoked like a function to retrieve posts.
 * It fetches the list of posts from the repository and returns it as a Flow.
 *
 * This use case is typically used in the ViewModel to handle the logic of fetching posts and observing them.
 */
class GetPostsUseCase @Inject constructor(
    private val postRepository: PostRepositoryInterface
) {

    /**
     * Retrieves the list of posts from the repository as a Flow.
     *
     * This method exposes the `posts` Flow from the repository, allowing observers to receive updates
     * when the list of posts changes.
     *
     * @return A Flow emitting the list of posts. The list will be updated in real-time, allowing
     *         the observer to react to changes automatically.
     */
    operator fun invoke(): Flow<List<Post>> {
        return postRepository.posts
    }
}
