package com.openclassrooms.hexagonal.games.utils.image

import android.util.Base64

object Base64Converter {

    // Convertir un ByteArray en une chaîne Base64
    fun toBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Convertir une chaîne Base64 en ByteArray
    fun fromBase64(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
    }
}