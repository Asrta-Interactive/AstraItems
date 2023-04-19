package com.astrainteractive.empireitems.desktop

import androidx.compose.ui.window.application
import com.astrainteractive.empireitems.desktop.di.componentContextModule
import com.astrainteractive.empireitems.desktop.di.lifecycleRegistryModule
import com.astrainteractive.empireitems.desktop.di.mainNavHostComponentModule
import com.astrainteractive.empireitems.desktop.presentation.application.MainWindow
import com.astrainteractive.empireitems.desktop.presentation.application.Tray
import com.astrainteractive.empireitems.desktop.utils.init

fun main() {
    lifecycleRegistryModule.init()
    componentContextModule.init()
    mainNavHostComponentModule.init()

    application {
        MainWindow()
        Tray()
    }

}


