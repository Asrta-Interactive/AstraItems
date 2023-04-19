package com.astrainteractive.empireitems.desktop.di

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.astrainteractive.empireitems.desktop.navigation.NavHostComponent
import com.astrainteractive.empireitems.desktop.navigation.Screen
import com.astrainteractive.empireitems.desktop.utils.runOnMainThreadBlocking
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.module

val lifecycleRegistryModule = module {
    LifecycleRegistry()
}
val componentContextModule = module {
    val lifecycle by lifecycleRegistryModule
    DefaultComponentContext(lifecycle)
}
val mainNavHostComponentModule = module {
    val componentContext by componentContextModule
    runOnMainThreadBlocking { NavHostComponent(componentContext) }
}
val applicationVisibilityState = module {
    mutableStateOf(true)
}
val stackNavigationModule = module {
    StackNavigation<Screen>()
}