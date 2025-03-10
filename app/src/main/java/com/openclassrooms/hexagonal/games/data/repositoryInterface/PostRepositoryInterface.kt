package com.openclassrooms.hexagonal.games.data.repositoryInterface

import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for managing posts.
 */
interface PostRepositoryInterface {

    /**
     * A Flow that emits a list of posts ordered by creation date in descending order.
     *
     * @return A [Flow] emitting a list of [Post] objects.
     */
    val posts: Flow<List<Post>>

    /**
     * Adds a new post to the repository.
     *
     * @param title The title of the post.
     * @param description An optional description of the post.
     * @param imageUri An optional URI of the image associated with the post.
     * @param authorId The unique identifier of the post author.
     * @param onSuccess A callback invoked when the post is successfully added.
     * @param onFailure A callback invoked when an error occurs, with the exception as a parameter.
     */
    suspend fun addPost(
        title: String,
        description: String?,
        imageUri: String?,
        authorId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    suspend fun getPostById(postId: String): Flow<Post?>
}