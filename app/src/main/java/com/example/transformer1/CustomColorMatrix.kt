package com.example.transformer1

import androidx.compose.ui.graphics.ColorMatrix


class CustomColorMatrix {
    companion object {
        fun createGrayscaleMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                setToSaturation(1f - i)
            }
        }

        fun createBrightnessMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                setToScale(1f + (0.2f * i), 1f + (0.2f * i), 1f + (0.2f * i), 1f)
            }
        }

        fun createSepiaMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(
                    ColorMatrix(floatArrayOf(
                        1f + (0.3f * i), (-0.3f * i), (0.1f * i), 0f, 0f,
                        0f, 1f + (0.3f * i), (0.2f * i), 0f, 0f,
                        0f, 0f, 0.8f + (0.2f * i), 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                )
            }
        }

        fun createInvertedMatrix(): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }

        fun createContrastMatrix(contrast: Float): ColorMatrix {
            val scale = contrast + 1f
            val translate = (-0.5f * scale + 0.5f) * 255f
            return ColorMatrix().apply {
                set(
                    ColorMatrix(floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
                )
            }
        }

        fun createWarmthMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    1f + (0.1f * i), 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f - (0.1f * i), 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }

        fun createCoolMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    1f - (0.1f * i), 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f + (0.1f * i), 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }

        fun createVintageMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    0.9f + (0.2f * i), 0.1f * i, 0.1f * i, 0f, 0f,
                    0.1f * i, 0.9f + (0.1f * i), 0.1f * i, 0f, 0f,
                    0.1f * i, 0.1f * i, 0.9f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }

        fun createDramaticMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    1.5f * i, -0.3f * i, -0.2f * i, 0f, 0f,
                    -0.3f * i, 1.5f * i, -0.2f * i, 0f, 0f,
                    -0.2f * i, -0.2f * i, 1.5f * i, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }

        fun createPurpleTintMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    1f, -0.2f * i, 0.2f * i, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0.2f * i, -0.2f * i, 1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }

        fun createHighContrastMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    1.5f * i, 0f, 0f, 0f, -32f * i,
                    0f, 1.5f * i, 0f, 0f, -32f * i,
                    0f, 0f, 1.5f * i, 0f, -32f * i,
                    0f, 0f, 0f, 1f, 0f
                ))
                )
            }
        }

        fun createFadedMatrix(i: Float): ColorMatrix {
            return ColorMatrix().apply {
                set(ColorMatrix(floatArrayOf(
                    1f - (0.1f * i), 0.2f * i, 0.2f * i, 0f, 20f * i,
                    0.2f * i, 1f - (0.1f * i), 0.2f * i, 0f, 20f * i,
                    0.2f * i, 0.2f * i, 1f - (0.1f * i), 0f, 20f * i,
                    0f, 0f, 0f, 1f, 0f
                )))
            }
        }
    }
}