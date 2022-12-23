package com.astrainteractive.empireitems.desktop.presentation.folder_selection

import com.astrainteractive.empireitems.desktop.mvi_core.Container
import com.astrainteractive.empireitems.desktop.mvi_core.IContainer
import com.astrainteractive.empireitems.desktop.mvi_core.reduce
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.state.FolderIntent
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.state.FolderSideEffect
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.state.FolderState
import com.astrainteractive.empireitems.desktop.utils.CoreViewModel
import java.io.File


class FolderSelectionViewModel : CoreViewModel<FolderState, FolderSideEffect, FolderIntent>() {
    override val container: IContainer<FolderState, FolderSideEffect> = Container(FolderState())

    override fun onIntent(intent: FolderIntent) {
        reduce { state ->
            when (intent) {
                FolderIntent.NavigateUp -> state.path.parentFile?.let { FolderState(it) } ?: state
                is FolderIntent.Selected -> {
                    FolderState(File(state.path, intent.path))
                }

                FolderIntent.SelectionCompleted -> {
                    FolderSideEffect.SelectionCompleted(state.path).sendSideEffectInScope()
                    state
                }
            }
        }
    }
}

