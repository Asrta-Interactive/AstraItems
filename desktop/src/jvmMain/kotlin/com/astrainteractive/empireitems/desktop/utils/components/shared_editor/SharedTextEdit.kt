package com.astrainteractive.empireitems.desktop.utils.components.shared_editor

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.astrainteractive.empireitems.desktop.style.Typography

@Composable
fun SharedTextEdit(value: String, onChanged: (String) -> Unit) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    TextField(
        value,
        onChanged,
        modifier = Modifier.fillMaxWidth(),
        textStyle = Typography.H3,
        interactionSource = interactionSource,
        colors = TextFieldDefaults.textFieldColors()
    )
}