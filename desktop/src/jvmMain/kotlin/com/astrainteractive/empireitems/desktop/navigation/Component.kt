package com.astrainteractive.empireitems.desktop.navigation


import androidx.compose.runtime.Composable
import com.astrainteractive.empireitems.desktop.di.mainNavHostComponentModule
import com.astrainteractive.empireitems.desktop.di.stackNavigationModule
import ru.astrainteractive.astralibs.di.getValue

interface Component {
    @Composable
    fun render()

    companion object {
        fun composeComponent(block: @Composable () -> Unit) = object : Component {
            @Composable
            override fun render() {
                block()
            }

        }
    }
}

abstract class LifecycleComponent : Component {
    val navHostComponent by mainNavHostComponentModule
    val stackNavigation by stackNavigationModule
}