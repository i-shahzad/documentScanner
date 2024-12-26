package com.example.transformer1

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.geometry.Size
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraManager(private val context: Context) {
    private var imageCapture: ImageCapture? = null

    suspend fun getCameraProvider(): ProcessCameraProvider {
        return suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(context).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(context))
            }
        }
    }

    fun takePhoto(
        context: Context,
        onPhotoCaptured: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            context.getExternalFilesDir(null),
            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { uri ->
                        onPhotoCaptured(uri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception.message ?: "Photo capture failed")
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        cameraProvider: ProcessCameraProvider,
        previewView: PreviewView
    ) {
        val screenMetrics = context.resources.displayMetrics
        val screenWidth = screenMetrics.widthPixels
        val targetHeight = (screenWidth * 4) / 3
        val targetResolution = android.util.Size(screenWidth, targetHeight)
        val aspectRatio = AspectRatio.RATIO_4_3

//        val screenRotation = when (context.display?.rotation) {
//            Surface.ROTATION_0 -> 0
//            Surface.ROTATION_90 -> 90
//            Surface.ROTATION_180 -> 180
//            Surface.ROTATION_270 -> 270
//            else -> 0
//        }

        val preview = Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
//            .setTargetRotation(screenRotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio)
//            .setTargetRotation(screenRotation)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }
}