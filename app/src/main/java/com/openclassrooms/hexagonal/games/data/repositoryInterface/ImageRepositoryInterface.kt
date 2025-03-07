package com.openclassrooms.hexagonal.games.data.repositoryInterface

import android.net.Uri
import com.google.android.gms.tasks.Task

interface ImageRepositoryInterface {

    fun uploadImage(uri: Uri): Task<Uri>
}