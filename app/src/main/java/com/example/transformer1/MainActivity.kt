package com.example.transformer1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                containerColor = Color.Black,
                bottomBar = {
                }
            ) { padding ->
                CameraApp(Modifier.padding(padding))
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    CameraPermissionHandler {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraManager = remember { CameraManager(context) }

        val previewView = remember { PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        } }

        LaunchedEffect(previewView) {
            val cameraProvider = cameraManager.getCameraProvider()
            cameraManager.startCamera(lifecycleOwner, cameraProvider, previewView)
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(4f / 3f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        cameraManager.takePhoto(
                            context = context,
                            onPhotoCaptured = onPhotoCaptured,
                            onError = onError
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Take Photo")
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CameraApp (modifier: Modifier = Modifier) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    if (capturedImageUri == null) {
        CameraScreen(
            onPhotoCaptured = { uri ->
                capturedImageUri = uri
            },
            onError = { error ->
                // Handle error
                println("Error capturing photo: $error")
            },
            modifier = modifier
        )
    } else {
        CapturedImageScreen(
            uri = capturedImageUri!!,
            onRetakePicture = {
                capturedImageUri = null
            },
            onAcceptPicture = { uri ->
                val intent = Intent(context, ImageTransformer::class.java)
                intent.putExtra("imageUri", uri.toString())
                context.startActivity(intent)
                println("Picture accepted: $uri")
            },
            modifier = modifier
        )
    }
}


@Composable
fun CapturedImageScreen(
    uri: Uri,
    onRetakePicture: () -> Unit,
    onAcceptPicture: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = loadAndRotateBitmap(uri)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            bitmap?.let { imageBitmap ->
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    // Delete img file
                    try {
                        context.contentResolver.delete(uri, null, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    onRetakePicture()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Retake")
            }

            Button(
                onClick = { onAcceptPicture(uri) }
            ) {
                Text("Accept")
            }
        }
    }
}

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


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionHandler(
    onPermissionGranted: @Composable () -> Unit
) {
    val cameraPermissonState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        if (!cameraPermissonState.status.isGranted) {
            cameraPermissonState.launchPermissionRequest()
        }
    }

    when {
        cameraPermissonState.status.isGranted -> {
            onPermissionGranted()
        }
        cameraPermissonState.status.shouldShowRationale -> {
            PermissionDialog(
                onRequestPermission = { cameraPermissonState.launchPermissionRequest() }
            )
        }
        else -> {
            PermissionDeniedContent()
        }
    }
}

@Composable
fun PermissionDeniedContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission is required for this feature to work. " +
                    "Please grant the permission in app settings.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                // Open app settings
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(this)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Open Settings")
        }
    }
}

@Composable
fun PermissionDialog(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission is required to take photos.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Request permission")
        }
    }
}


