package com.openclassrooms.hexagonal.games.data.repository



import android.net.Uri
import com.google.android.gms.tasks.Task
import com.openclassrooms.hexagonal.games.data.repositoryInterface.ImageRepositoryInterface
import com.openclassrooms.hexagonal.games.data.service.firebase.FirebaseImageApi
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val firebaseImageApi: FirebaseImageApi
) : ImageRepositoryInterface {
    override fun uploadImage(uri: Uri): Task<Uri> {
        return firebaseImageApi.uploadImage(uri)
    }
}
