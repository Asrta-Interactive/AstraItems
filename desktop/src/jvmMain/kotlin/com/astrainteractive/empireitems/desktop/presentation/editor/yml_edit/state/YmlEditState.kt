package com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state

import com.atrainteractive.empire_items.models.ItemYamlFile
import java.io.File

sealed interface YmlEditState {
    val file: File

    class Loading(override val file: File) : YmlEditState
    data class Parsed(override val file: File, val itemYamlFile: ItemYamlFile) : YmlEditState
    class Error(override val file: File) : YmlEditState
}