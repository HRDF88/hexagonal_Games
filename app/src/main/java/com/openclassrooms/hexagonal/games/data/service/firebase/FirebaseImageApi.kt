package com.openclassrooms.hexagonal.games.data.service.firebase

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseImageApi {

    private val storageRef = FirebaseStorage.getInstance().reference

    fun uploadImage(uri: Uri): Task<Uri> {
        val fileRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        Log.d("FirebaseImageApi", "Uploading image with URI: $uri")
        Log.d("FirebaseImageApi", "Uploading to: ${fileRef.path}")
        return fileRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Image upload failed")
                }
                fileRef.downloadUrl
            }
    }

}
