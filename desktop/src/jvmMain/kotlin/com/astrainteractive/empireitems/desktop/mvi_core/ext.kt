package com.astrainteractive.empireitems.desktop.mvi_core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.updateAndGet

inline fun <reified T : Any> ContainerHost<T, *, *>.reduce(block: (T) -> T): T {
    return this.container.stateFlow.updateAndGet(block)
}

inline fun <reified T : Any, K : T> ContainerHost<T, *, *>.reduceState(block: (K) -> T): T {
    return this.container.stateFlow.updateAndGet {
        (it as? K)?.let(block) ?: it
    }
}

suspend fun <T : Any> ContainerHost<*, T, *>.sideEffect(effect: T) {
    return this.container.send(effect)
}

@Composable
fun <T : Any> ContainerHost<T, *, *>.collectState(): State<T> = this.container.stateFlow.collectAsState()


@Composable
inline fun <reified T : Any> ContainerHost<*, T, *>.observeSideEffect(crossinline onSideEffect: (T) -> Unit) {
    LaunchedEffect(this) {
        container.sideEffectFlow.collectLatest {
            it?.let(onSideEffect)
        }
    }
}