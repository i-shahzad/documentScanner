package com.example.transformer1

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

class ImageFiltersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val photoUriString = intent.getStringExtra("imageUri")
        val photoUri = Uri.parse(photoUriString)
        setContent {
            Scaffold(
                containerColor = Color.Black,
                bottomBar = {
                }
            ) { padding ->
                ImageFilterScreen(Modifier.padding(padding), photoUri)
            }
        }
    }
}

@Composable
fun ImageFilterScreen(modifier: Modifier = Modifier, photoUri: Uri) {
    val bitmap = loadAndRotateBitmap(photoUri)
    var selectedFilter by remember { mutableStateOf(ImageFilter.NORMAL) }
    var intensity by remember { mutableStateOf(1f) }

    LaunchedEffect(selectedFilter) {
        intensity = 1f
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
//                .weight(1f)
                .fillMaxWidth()
        ) {
            bitmap?.let { imageBitmap ->
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                    colorFilter = when (selectedFilter) {
                        ImageFilter.NORMAL -> null
                        ImageFilter.GRAYSCALE -> ColorFilter.colorMatrix(CustomColorMatrix.createGrayscaleMatrix(intensity))
                        ImageFilter.SEPIA -> ColorFilter.colorMatrix(CustomColorMatrix.createSepiaMatrix(intensity))
                        ImageFilter.INVERTED -> ColorFilter.colorMatrix(CustomColorMatrix.createInvertedMatrix())
                        ImageFilter.BRIGHTNESS -> ColorFilter.colorMatrix(CustomColorMatrix.createBrightnessMatrix(intensity))
                        ImageFilter.HIGH_CONTRAST -> ColorFilter.colorMatrix(CustomColorMatrix.createHighContrastMatrix(intensity))
                        ImageFilter.DRAMATIC -> ColorFilter.colorMatrix(CustomColorMatrix.createDramaticMatrix(intensity))
                        ImageFilter.PURPLE_TINT -> ColorFilter.colorMatrix(CustomColorMatrix.createPurpleTintMatrix(intensity))
                        ImageFilter.FADED -> ColorFilter.colorMatrix(CustomColorMatrix.createFadedMatrix(intensity))
                        ImageFilter.COOL -> ColorFilter.colorMatrix(CustomColorMatrix.createCoolMatrix(intensity))
                        ImageFilter.WARMTH -> ColorFilter.colorMatrix(CustomColorMatrix.createWarmthMatrix(intensity))
                        ImageFilter.VINTAGE -> ColorFilter.colorMatrix(CustomColorMatrix.createVintageMatrix(intensity))
                    }
                )
            }
        }
        if (selectedFilter != ImageFilter.NORMAL && selectedFilter != ImageFilter.INVERTED) {
            Spacer(Modifier.height(8.dp))
            Column (

            ) {
                Text(
                    text = "Intensity ",
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Slider(
                        value = intensity,
                        onValueChange = { intensity = it },
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(9f)
                    )
                    Text(
                        text = "${(intensity * 100).toInt()}%",
                        modifier = Modifier
                            .padding(horizontal = 1.dp)
                            .weight(2f),
                        color = Color.White
                    )
                }
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            items(ImageFilter.values()) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.name) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

