package com.astrainteractive.empireitems.desktop.presentation.folder_selection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens
import com.astrainteractive.empireitems.desktop.style.Typography
import java.io.File

@Composable
fun FileRow(file: File, isSelected: Boolean = false, onClicked: () -> Unit) {
    val icon = if (file.isDirectory) Icons.Filled.Folder
    else Icons.Filled.Article
    val color = if (file.isDirectory) Colors.colorSecondary
    else Colors.colorHint
    val textColor = if (file.isDirectory) Colors.colorOnPrimary
    else Colors.colorHint
    Row(
        Modifier.fillMaxWidth()
            .background(if (isSelected) Colors.colorSecondary else Colors.Transparent).clickable { onClicked() }.padding(horizontal = Dimens.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, "", modifier = Modifier.size(36.dp), tint = color)
        Spacer(Modifier.width(Dimens.S))
        Text(file.name, style = Typography.H3.copy(color = textColor))
    }
}