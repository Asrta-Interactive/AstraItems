package com.astrainteractive.empireitems.desktop.presentation.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.astrainteractive.empireitems.desktop.style.Colors
import io.kanro.compose.jetbrains.expui.window.MainToolBarScope

@Composable
fun ToolBar(): @Composable MainToolBarScope.() -> Unit = {
    Row(
        Modifier.mainToolBarItem(
            Alignment.Start,
            true
        ).background(Colors.colorPrimaryVariant)
    ) {
    }
}