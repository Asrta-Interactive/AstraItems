package com.astrainteractive.empireitems.desktop.presentation.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.astrainteractive.empireitems.desktop.di.applicationVisibilityState
import com.astrainteractive.empireitems.desktop.di.lifecycleRegistryModule
import com.astrainteractive.empireitems.desktop.di.mainNavHostComponentModule
import com.astrainteractive.empireitems.desktop.resources.Resources
import io.kanro.compose.jetbrains.expui.theme.LightTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import ru.astrainteractive.astralibs.di.getValue

@Composable
fun MainWindow() {
    var isApplicationVisible by remember { applicationVisibilityState.value }
    val windowState = rememberWindowState(size = DpSize(900.dp, 700.dp))
    val lifecycle by lifecycleRegistryModule
    val mainNavHostComponent by mainNavHostComponentModule
    JBWindow(
        title = "EmpireItems Desktop",
        showTitle = true, // If you want to render your own component in the center of the title bar like Intellij do, disable this to hide the title of the MainToolBar (TitleBar).
        theme = LightTheme, // Change the theme here, LightTheme and DarkTheme are provided.
        state = windowState,
        visible = isApplicationVisible,
        onCloseRequest = { isApplicationVisible = false },
        icon = Resources.logo.painter,
        mainToolBar = ToolBar()
    ) {
        LifecycleController(lifecycle, windowState)
        mainNavHostComponent.render()
    }
}