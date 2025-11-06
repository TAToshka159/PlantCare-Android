// FileUtil.kt
package com.example.plantcare.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

object FileUtil {
    fun saveImageFromUri(context: Context, uri: Uri, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
            val cacheDir = File(context.cacheDir, "plant_photos")
            cacheDir.mkdirs()
            val outFile = File(cacheDir, fileName)
            outFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            outFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}