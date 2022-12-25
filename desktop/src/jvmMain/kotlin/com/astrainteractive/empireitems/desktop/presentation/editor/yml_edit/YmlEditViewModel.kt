package com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit

import com.astrainteractive.empireitems.desktop.mvi_core.Container
import com.astrainteractive.empireitems.desktop.mvi_core.IContainer
import com.astrainteractive.empireitems.desktop.mvi_core.reduce
import com.astrainteractive.empireitems.desktop.mvi_core.reduceState
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state.YmlEditIntent
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state.YmlEditSideEffect
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state.YmlEditState
import com.astrainteractive.empireitems.desktop.utils.CoreViewModel
import com.astrainteractive.empireitems.desktop.utils.toClass
import com.atrainteractive.empire_items.models.ItemYamlFile
import java.io.File

class YmlEditViewModel(val file: File) : CoreViewModel<YmlEditState, YmlEditSideEffect, YmlEditIntent>() {
    override val container: IContainer<YmlEditState, YmlEditSideEffect> = Container(YmlEditState.Loading(file))
    override fun onIntent(intent: YmlEditIntent) {
        reduceState { it: YmlEditState.Parsed ->
            when (intent) {
                is YmlEditIntent.NamespaceChanged -> it.copy(itemYamlFile = it.itemYamlFile.copy(namespace = intent.namespace.replace(" ","")))
            }
        }
    }

    init {
        reduce {
            toClass<ItemYamlFile>(file)?.let {
                YmlEditState.Parsed(file, it)
            } ?: YmlEditState.Error(file)
        }
    }
}
