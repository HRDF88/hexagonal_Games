package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * This interface defines the contract for interacting with Post data from a data source.
 * It outlines the methods for retrieving and adding Posts, abstracting the underlying
 * implementation details of fetching and persisting data.
 */
interface PostApi {


  fun getPosts(): Flow<List<Post>>

  /**
   * Adds a new Post to the data source.
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
  )
}
