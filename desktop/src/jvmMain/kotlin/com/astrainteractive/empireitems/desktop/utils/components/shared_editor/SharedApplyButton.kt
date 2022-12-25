package com.astrainteractive.empireitems.desktop.utils.components.shared_editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens
import com.astrainteractive.empireitems.desktop.style.Typography

@Composable
fun SharedApplyButton(onClick: () -> Unit) {
    Box(Modifier.fillMaxSize().zIndex(Float.MAX_VALUE), contentAlignment = Alignment.BottomEnd) {
        Button(
            onClick,
            modifier = Modifier.clip(RoundedCornerShape(Dimens.S)).padding(Dimens.S),
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.colorSecondary)
        ) {
            Text("Apply", style = Typography.H3)
        }
    }

}