package com.astrainteractive.empireitems.desktop.presentation.folder_selection.state

import java.io.File

sealed interface FolderSideEffect {
    class SelectionCompleted(val path: File) : FolderSideEffect
}