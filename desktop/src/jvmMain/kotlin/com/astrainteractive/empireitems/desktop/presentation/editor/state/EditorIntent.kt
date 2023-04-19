package com.astrainteractive.empireitems.desktop.presentation.editor.state

import java.io.File

sealed interface EditorIntent {
    class FileSelected(val file: File) : EditorIntent
}