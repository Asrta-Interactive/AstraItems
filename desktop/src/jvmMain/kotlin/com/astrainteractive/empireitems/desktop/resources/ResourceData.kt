package com.astrainteractive.empireitems.desktop.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource

sealed class ResourceData<T>(val path: String) {
    class Bitmap(path: String) : ResourceData<ImageBitmap>(path) {
        val bitmap: ImageBitmap
            get() = useResource(path, ::loadImageBitmap)
        val painter: Painter
            @Composable
            get() = painterResource(path)
    }

    class Svg(path: String) : ResourceData<ImageBitmap>(path) {
        val svg: Painter
            @Composable
            get() = painterResource(path)
    }

}