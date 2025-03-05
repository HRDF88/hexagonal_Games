package com.openclassrooms.hexagonal.games.data.repository

import android.net.Uri
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class provides a repository for accessing and managing Post data.
 * It utilizes dependency injection to retrieve a PostApi instance for interacting
 * with the data source. The class is marked as a Singleton using @Singleton annotation,
 * ensuring there's only one instance throughout the application.
 */
@Singleton
class PostRepository @Inject constructor(private val postApi: PostApi) {

    /**
     * Retrieves a Flow object containing a list of Posts ordered by creation date
     * in descending order.
     *
     * @return Flow containing a list of Posts.
     */
    val posts: Flow<List<Post>> = postApi.getPosts()

    /**
     * Adds a new Post to the data source using the injected PostApi.
     *
     * @param post The Post object to be added.
     */
    suspend fun addPost(
        title: String,
        description: String?,
        imageUri: Uri?,
        authorId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        postApi.addPost(title, description, imageUri, authorId, onSuccess, onFailure)
    }

}
