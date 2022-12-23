package com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.astrainteractive.empireitems.desktop.mvi_core.collectState
import com.astrainteractive.empireitems.desktop.navigation.Screen
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.components.YamlSection
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.state.YmlEditState
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens
import java.io.File

@Composable
fun YmlEditScreen(componentContext: ComponentContext, navigation: StackNavigation<Screen>, file: File) {
    val viewModel = componentContext.instanceKeeper.getOrCreate(file) {
        YmlEditViewModel(file)
    }
    val state by viewModel.collectState()

    when (val state = state) {
        is YmlEditState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error validation file")
            }
        }

        is YmlEditState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(54.dp), color = Colors.colorSecondary)
            }
        }

        is YmlEditState.Parsed -> {
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(Dimens.S)) {
                item {
                    YamlSection(state.itemYamlFile::namespace) {
                        navigation.push(Screen.NamespaceEditor(viewModel))
                    }
                }
                item {
                    YamlSection(state.itemYamlFile::crafting_table) {}
                    componentContext
                }
                item { YamlSection(state.itemYamlFile::shapeless) {} }
                item { YamlSection(state.itemYamlFile::furnace) {} }
                item { YamlSection(state.itemYamlFile::loot) {} }
                item { YamlSection(state.itemYamlFile::villagerTrades) {} }
                item { YamlSection(state.itemYamlFile::fontImages) {} }
                item { YamlSection(state.itemYamlFile::merchant_recipes) {} }
                item { YamlSection(state.itemYamlFile::yml_items) {} }
                item { YamlSection(state.itemYamlFile::ymlSounds) {} }
                item { YamlSection(state.itemYamlFile::ymlMob) {} }
            }
        }
    }
}