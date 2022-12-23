package com.astrainteractive.empireitems.desktop.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astrainteractive.empireitems.desktop.style.Colors


@Composable
fun SplitScreen(
    aspectRatio: Float,
    appBar: @Composable RowScope.() -> Unit = {},
    leftScreen: @Composable BoxScope.() -> Unit,
    rightScreen: @Composable BoxScope.() -> Unit
) {
    Row(Modifier.fillMaxSize().background(Colors.colorPrimaryVariant)) {
        appBar(this)
        Box(
            Modifier.fillMaxWidth(aspectRatio).background(Colors.colorPrimaryVariant),
            content = leftScreen
        )
        Box(
            Modifier.fillMaxWidth().background(Colors.colorPrimaryVariant),
            content = rightScreen
        )
    }
}