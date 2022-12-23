package com.astrainteractive.empireitems.desktop.presentation.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.astrainteractive.empireitems.desktop.mvi_core.collectState
import com.astrainteractive.empireitems.desktop.navigation.Component
import com.astrainteractive.empireitems.desktop.navigation.Screen
import com.astrainteractive.empireitems.desktop.presentation.editor.state.EditorIntent
import com.astrainteractive.empireitems.desktop.presentation.editor.state.EditorState
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.YmlEditScreen
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.components.FileRow
import com.astrainteractive.empireitems.desktop.style.Typography
import com.astrainteractive.empireitems.desktop.utils.components.AppBarIcon
import com.astrainteractive.empireitems.desktop.utils.components.SplitScreen
import com.astrainteractive.empireitems.desktop.utils.components.VerticalAppBar
import java.io.File

class EditorScreen(
    val componentContext: ComponentContext,
    val navigation: StackNavigation<Screen>,
    val path: File
) : Component {
    private val viewModel = componentContext.instanceKeeper.getOrCreate {
        EditorViewModel(path)
    }

    @Composable
    override fun render() {
        val state by viewModel.collectState()
        val selectedState = state as? EditorState.Selected

        SplitScreen(0.2f,
            appBar = { AppBar(componentContext, navigation) },
            leftScreen = {
                val ymlFiles = path.listFiles()?.filter { it.extension == "yml" } ?: emptyList()
                LazyColumn(Modifier.fillMaxSize()) {
                    items(ymlFiles) {
                        val isCurrentFileSelected = selectedState?.file?.name == it.name
                        FileRow(it, isCurrentFileSelected) {
                            viewModel.onIntent(EditorIntent.FileSelected(it))
                        }
                    }
                }
            }, rightScreen = {
                when (val state = state) {
                    EditorState.Pending -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Choose .yml file on the left screen", style = Typography.H1)
                        }
                    }

                    is EditorState.Selected -> YmlEditScreen(componentContext,navigation,state.file)
                }
            })
    }
}


@Composable
fun AppBar(
    componentContext: ComponentContext,
    navigation: StackNavigation<Screen>,
) {
    VerticalAppBar {
        AppBarIcon(Icons.Default.Menu) {

        }
        AppBarIcon(Icons.Default.Public) {
            navigation.replaceCurrent(Screen.FolderSelection)
        }
    }
}

