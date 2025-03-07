package com.openclassrooms.hexagonal.games.utils.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object BitmapConverter {

    private const val qualityConvert = SizeBitmapCONST.quality

    // Convertir un Bitmap en ByteArray (avec un format et une qualité personnalisables)
    fun toByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = qualityConvert): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, outputStream)
        return outputStream.toByteArray()
    }

    // Convertir un ByteArray en Bitmap
    fun fromByteArray(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}