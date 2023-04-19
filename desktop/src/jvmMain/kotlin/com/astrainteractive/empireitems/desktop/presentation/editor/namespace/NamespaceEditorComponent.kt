package com.astrainteractive.empireitems.desktop.presentation.editor.namespace

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.pop
import com.astrainteractive.empireitems.desktop.mvi_core.collectState
import com.astrainteractive.empireitems.desktop.navigation.LifecycleComponent
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.YmlEditViewModel
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state.YmlEditIntent
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state.YmlEditState
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens
import com.astrainteractive.empireitems.desktop.style.Typography
import com.astrainteractive.empireitems.desktop.utils.components.shared_editor.SharedApplyButton
import com.astrainteractive.empireitems.desktop.utils.components.shared_editor.SharedTextEdit
import com.astrainteractive.empireitems.desktop.utils.components.shared_editor.SharedTitle

class NamespaceEditorComponent(private val ymlEditViewModel: YmlEditViewModel) : LifecycleComponent() {

    @Composable
    override fun render() {
        val _state by ymlEditViewModel.collectState()
        val state = _state as? YmlEditState.Parsed ?: return
        val namespace = state.itemYamlFile.namespace
        SharedApplyButton {}
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton({
                    stackNavigation.pop()
                }) {
                    Icon(Icons.Filled.ChevronLeft, "", tint = Colors.colorOnPrimary)
                }
                Text("${state.file.name}: namespace", style = Typography.H1)
            }
            Column(Modifier.fillMaxSize().padding(Dimens.L)) {
                SharedTitle("Editing \"namespace\" field")
                SharedTextEdit(namespace) {

                    ymlEditViewModel.onIntent(YmlEditIntent.NamespaceChanged(it))
                }
            }
        }


    }

}





