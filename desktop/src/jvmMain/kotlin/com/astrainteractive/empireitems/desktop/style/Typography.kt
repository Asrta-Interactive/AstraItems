package com.astrainteractive.empireitems.desktop.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

object Typography {
    val H1: TextStyle
        @Composable
        get() = TextStyle(
            color = Colors.colorOnPrimary,
            fontSize = TextSizes.XL,
            fontWeight = FontWeight.Bold
        )
    val H3: TextStyle
        @Composable
        get() = TextStyle(
            color = Colors.colorOnPrimary,
            fontSize = TextSizes.M,
        )
    val Default: TextStyle
        @Composable
        get() = TextStyle(
            color = Colors.colorOnPrimary,
            fontSize = TextSizes.M,
        )
    val Hint: TextStyle
        @Composable
        get() = TextStyle(
            color = Colors.colorHint,
            fontSize = TextSizes.S,
        )
}