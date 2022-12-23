package com.astrainteractive.empireitems.desktop.presentation.editor

import com.astrainteractive.empireitems.desktop.mvi_core.Container
import com.astrainteractive.empireitems.desktop.mvi_core.IContainer
import com.astrainteractive.empireitems.desktop.mvi_core.reduce
import com.astrainteractive.empireitems.desktop.presentation.editor.state.EditorIntent
import com.astrainteractive.empireitems.desktop.presentation.editor.state.EditorSideEffect
import com.astrainteractive.empireitems.desktop.presentation.editor.state.EditorState
import com.astrainteractive.empireitems.desktop.utils.CoreViewModel
import java.io.File

class EditorViewModel(path: File) : CoreViewModel<EditorState, EditorSideEffect, EditorIntent>() {
    override val container: IContainer<EditorState, EditorSideEffect> = Container(EditorState.Pending)

    override fun onIntent(intent: EditorIntent) {
        reduce {
            when (intent) {
                is EditorIntent.FileSelected -> EditorState.Selected(intent.file)
            }
        }
    }
}