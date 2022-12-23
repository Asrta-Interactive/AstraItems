package com.astrainteractive.empireitems.desktop.utils

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.astrainteractive.empireitems.desktop.mvi_core.ContainerHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.architecture.ViewModel

abstract class CoreViewModel<STATE : Any, SIDE_EFFECT : Any, INTENTS : Any> : InstanceKeeper.Instance,
    ContainerHost<STATE, SIDE_EFFECT, INTENTS>, ViewModel() {
    fun SIDE_EFFECT.sendSideEffectInScope() = viewModelScope.launch(Dispatchers.IO) {
        container.send(this@sendSideEffectInScope)
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}