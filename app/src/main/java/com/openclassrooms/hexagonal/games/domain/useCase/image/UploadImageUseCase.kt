package com.openclassrooms.hexagonal.games.domain.useCase

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.openclassrooms.hexagonal.games.data.repositoryInterface.ImageRepositoryInterface
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepositoryInterface
) {
    operator fun invoke(uri: Uri): Task<Uri> {
        return imageRepository.uploadImage(uri)
    }
}