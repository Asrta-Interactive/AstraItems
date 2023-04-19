package com.astrainteractive.empireitems.desktop.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.astrainteractive.empireitems.desktop.style.Colors

@Composable
fun VerticalAppBar(actions: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.width(54.dp).fillMaxSize().background(Colors.colorPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        actions(this)
    }
}