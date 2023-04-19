package com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens
import com.astrainteractive.empireitems.desktop.style.Typography
import kotlin.reflect.KProperty

@Composable
fun <T> YamlSection(property: KProperty<T>, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.S)).background(Colors.colorPrimary).clickable(onClick = onClick)
            .padding(horizontal = Dimens.M, vertical = Dimens.S),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(property.name, style = Typography.H3)
        Icon(Icons.Filled.ChevronRight, "", Modifier.padding(Dimens.S), tint = Colors.colorOnPrimary)
    }

}