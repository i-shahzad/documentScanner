package com.example.transformer1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.IOException

@Composable
fun loadAndRotateBitmap(uri: Uri): ImageBitmap? {
    val context = LocalContext.current

    return remember(uri) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // First, decode the image
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Get rotation from EXIF data
            val rotation = try {
                context.contentResolver.openInputStream(uri)?.use { exifStream ->
                    val exif = ExifInterface(exifStream)
                    when (exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                } ?: 0
            } catch (e: IOException) {
                e.printStackTrace()
                0
            }

            // If rotation is needed, rotate the bitmap
            val transformedBitmap = if (rotation != 0) {
                val matrix = Matrix().apply {
                    postRotate(rotation.toFloat())
                }
                Bitmap.createBitmap(
                    originalBitmap,
                    0,
                    0,
                    originalBitmap.width,
                    originalBitmap.height,
                    matrix,
                    true
                )
            } else {
                originalBitmap
            }

            transformedBitmap.asImageBitmap()
        }
    }
}
