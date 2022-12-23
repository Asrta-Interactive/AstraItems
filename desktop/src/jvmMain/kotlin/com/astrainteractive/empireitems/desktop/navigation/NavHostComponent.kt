package com.astrainteractive.empireitems.desktop.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.childStack
import com.astrainteractive.empireitems.desktop.di.stackNavigationModule
import com.astrainteractive.empireitems.desktop.presentation.editor.EditorScreen
import com.astrainteractive.empireitems.desktop.presentation.editor.namespace.NamespaceEditorComponent
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.FolderSelectorScreen
import com.astrainteractive.empireitems.desktop.presentation.splash.SplashScreen
import com.astrainteractive.empireitems.desktop.style.Colors
import ru.astrainteractive.astralibs.di.getValue

/**
 * Navigator
 */
class NavHostComponent(
    componentContext: ComponentContext
) : Component, ComponentContext by componentContext {
    private val navigation by stackNavigationModule
    private val stack = childStack(
        source = navigation,
        initialConfiguration = Screen.Initial,
        childFactory = ::createScreenComponent
    )


    /**
     * Factory function to create screen from given ScreenConfig
     */
    private fun createScreenComponent(
        screenConfig: Screen,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            is Screen.Splash -> SplashScreen(componentContext, navigation)
            is Screen.FolderSelection -> FolderSelectorScreen(componentContext, navigation)
            is Screen.Editor -> EditorScreen(componentContext, navigation, screenConfig.path)
            is Screen.NamespaceEditor -> NamespaceEditorComponent(screenConfig.viewModel)
        }
    }

    /**
     * Renders screen as per request
     */
    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    override fun render() {
        Children(
            stack = stack,
            animation = stackAnimation(fade() + scale()),
            modifier = Modifier.fillMaxSize().background(Colors.colorPrimaryVariant)
        ) {
            it.instance.render()
        }
    }


}