package com.example.transformer1

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

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
