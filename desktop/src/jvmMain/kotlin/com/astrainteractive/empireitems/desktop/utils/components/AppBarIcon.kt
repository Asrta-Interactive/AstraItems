package com.astrainteractive.empireitems.desktop.utils.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens

@Composable
fun AppBarIcon(imageVector: ImageVector, onClick: () -> Unit) {
    Icon(
        imageVector,
        "",
        tint = Colors.colorHint,
        modifier = Modifier.clickable(onClick = onClick).padding(Dimens.S)
    )
}