package com.openclassrooms.hexagonal.games.domain.useCase.post

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Flow<Post?> {
        return postRepository.getPostById(postId)
    }
}
