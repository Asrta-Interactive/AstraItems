package com.astrainteractive.empireitems.desktop.presentation.folder_selection.state

sealed interface FolderIntent {
    class Selected(val path: String) : FolderIntent
    object NavigateUp : FolderIntent
    object SelectionCompleted : FolderIntent
}