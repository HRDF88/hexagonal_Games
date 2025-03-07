package com.openclassrooms.hexagonal.games.data.service.serviceInterface

import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * This interface defines the contract for interacting with Post data from a data source.
 * It outlines the methods for retrieving and adding Posts, abstracting the underlying
 * implementation details of fetching and persisting data.
 */
interface PostApi {

    /**
     * Retrieves a list of posts from the data source as a Flow.
     *
     * @return A Flow emitting a list of posts. The list may be empty if no posts are available.
     *         The data is typically updated in real-time, allowing observers to receive updates.
     */
    fun getPosts(): Flow<List<Post>>

    /**
     * Adds a new Post to the data source.
     *
     * @param title The title of the post.
     * @param description An optional description for the post. Can be null if no description is provided.
     * @param imageUri The URI pointing to an image to be associated with the post. Can be null if no image is provided.
     * @param authorId The ID of the author creating the post.
     * @param onSuccess A callback that is invoked when the post is successfully added to the data source.
     * @param onFailure A callback that is invoked if an error occurs while adding the post. It receives the exception.
     */
    suspend fun addPost(
        title: String,
        description: String?,
        imageUri: String?,
        authorId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}
