package com.astrainteractive.empireitems.desktop.utils.components.shared_editor

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.astrainteractive.empireitems.desktop.style.Typography

@Composable
fun SharedTitle(value: String) {
    Text(value, style = Typography.H1)
}