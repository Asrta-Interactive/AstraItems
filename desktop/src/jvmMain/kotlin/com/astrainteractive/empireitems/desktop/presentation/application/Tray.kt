package com.astrainteractive.empireitems.desktop.presentation.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import com.astrainteractive.empireitems.desktop.di.applicationVisibilityState
import com.astrainteractive.empireitems.desktop.resources.Resources

@Composable
fun ApplicationScope.Tray() {
    var isApplicationVisible by remember { applicationVisibilityState.value }
    if (!isApplicationVisible) {

        Tray(
            Resources.logo.painter,
            tooltip = "EmpireItems Desktop",
            onAction = { isApplicationVisible = true },
            menu = {
                Item("Close", onClick = ::exitApplication)
                Item("Open", onClick = { isApplicationVisible = true })
            },
        )
    }
}