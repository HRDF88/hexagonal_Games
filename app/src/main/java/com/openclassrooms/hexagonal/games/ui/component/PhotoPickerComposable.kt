package com.openclassrooms.hexagonal.games.ui.component

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun PhotoPickerComposable(
    imageUri: Uri?,
    onImageUriChanged: (Uri?) -> Unit
) {
    // Enregistrer le résultat de l'activité de sélection de photo
    val pickMedia = rememberLauncherForActivityResult(
        contract = PickVisualMedia(), // Choisir l'image (ou la vidéo)
        onResult = { uri ->
            // Callback pour traiter le résultat
            if (uri != null) {
                onImageUriChanged(uri) // Mettre à jour `imageUri` dans le composant parent
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    )

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Affichage de l'image sélectionnée ou d'un bouton
        if (imageUri != null) {
            // Utiliser Coil pour afficher l'image sélectionnée à partir de l'URI
            val painter = rememberAsyncImagePainter(imageUri)
            Image(painter = painter, contentDescription = "Selected Image", modifier = Modifier.size(200.dp))
        } else {
            // Affichage d'un message si aucune image n'est sélectionnée
            Text("Aucune image sélectionnée")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour ouvrir le Photo Picker
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) // Lancer le picker pour sélectionner uniquement des images
        }) {
            Text("Sélectionner une photo")
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PhotoPickerPreview() {
    PhotoPickerComposable(imageUri = null, onImageUriChanged = {})
}
