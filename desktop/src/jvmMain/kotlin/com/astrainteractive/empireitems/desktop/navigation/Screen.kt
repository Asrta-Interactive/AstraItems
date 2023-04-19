package com.astrainteractive.empireitems.desktop.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.astrainteractive.empireitems.desktop.presentation.editor.yml_edit.YmlEditViewModel
import java.io.File

sealed interface Screen : Parcelable {
    companion object {
        val Initial: Screen
            get() = Editor(File("D:\\Minecraft Servers\\EmpireProjekt_remake\\smp\\plugins\\EmpireItems\\items"))
    }

    @Parcelize
    object Splash : Screen

    @Parcelize
    class Editor(val path: File) : Screen
    @Parcelize
    class NamespaceEditor(val viewModel:YmlEditViewModel) : Screen

    @Parcelize
    object FolderSelection : Screen

}