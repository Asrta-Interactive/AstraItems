package com.astrainteractive.empireitems.desktop.presentation.editor.state

import java.io.File

sealed interface EditorState {
    object Pending : EditorState
    class Selected(val file: File) : EditorState
}