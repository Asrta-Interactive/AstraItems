package com.astrainteractive.empireitems.desktop.presentation.folder_selection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.astrainteractive.empireitems.desktop.mvi_core.collectState
import com.astrainteractive.empireitems.desktop.mvi_core.observeSideEffect
import com.astrainteractive.empireitems.desktop.navigation.Component
import com.astrainteractive.empireitems.desktop.navigation.Screen
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.components.FodlerSelector
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.state.FolderIntent
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.state.FolderSideEffect
import com.astrainteractive.empireitems.desktop.style.Dimens
import com.astrainteractive.empireitems.desktop.style.Typography
import io.kanro.compose.jetbrains.expui.control.PrimaryButton

class FolderSelectorScreen(
    val componentContext: ComponentContext,
    val navigation: StackNavigation<Screen>
) : Component {
    @Composable
    override fun render() {
        val viewModel = componentContext.instanceKeeper.getOrCreate {
            FolderSelectionViewModel()
        }
        val state by viewModel.collectState()
        viewModel.observeSideEffect {
            when (it) {
                is FolderSideEffect.SelectionCompleted -> {
                    navigation.replaceCurrent(Screen.Editor(it.path))
                }
            }

        }
        Box(Modifier.fillMaxSize().zIndex(Float.MAX_VALUE), contentAlignment = Alignment.BottomEnd) {
            PrimaryButton({
                viewModel.onIntent(FolderIntent.SelectionCompleted)
            }, modifier = Modifier.padding(Dimens.S)) {
                Text("Apply", style = Typography.H3)
            }
        }
        FodlerSelector(state, onFolderSelected = {
            viewModel.onIntent(FolderIntent.Selected(it.name))
        }, onBackClicked = {
            viewModel.onIntent(FolderIntent.NavigateUp)
        })

    }

}