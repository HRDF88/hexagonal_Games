package com.openclassrooms.hexagonal.games.domain.UseCase

import com.openclassrooms.hexagonal.games.data.repositoryInterface.PostRepositoryInterface
import javax.inject.Inject

/**
 * This class encapsulates the use case for adding a new post to the data source.
 * It is responsible for handling the logic of adding a post by interacting with the
 * PostRepositoryInterface.
 *
 * The class defines an operator function `invoke` that allows it to be invoked like a function.
 * It takes the necessary parameters for creating a post and delegates the actual addition
 * of the post to the repository.
 *
 * This class is typically used in the ViewModel to abstract the business logic for adding a post.
 */
class AddPostUseCase @Inject constructor(
    private val postRepository: PostRepositoryInterface
) {

    /**
     * Adds a new post by invoking the addPost function in the repository.
     *
     * @param title The title of the post to be added.
     * @param description An optional description for the post. It can be null if not provided.
     * @param imageUri An optional URI pointing to an image. It can be null if no image is provided.
     * @param authorId The ID of the author creating the post.
     * @param onSuccess A callback function that is invoked when the post is successfully added.
     * @param onFailure A callback function that is invoked if an error occurs during the post addition.
     */
    suspend operator fun invoke(
        title: String,
        description: String?,
        imageUri: String?,
        authorId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        postRepository.addPost(title, description, imageUri, authorId, onSuccess, onFailure)
    }
}
