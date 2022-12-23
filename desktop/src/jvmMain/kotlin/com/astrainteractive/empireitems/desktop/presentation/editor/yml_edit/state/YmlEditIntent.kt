package com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state

sealed interface YmlEditIntent {
    class NamespaceChanged(val namespace: String) : YmlEditIntent
}