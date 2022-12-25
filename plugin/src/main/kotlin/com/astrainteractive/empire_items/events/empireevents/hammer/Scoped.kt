package com.astrainteractive.empire_items.events.empireevents.hammer

import kotlinx.coroutines.*
import ru.astrainteractive.astralibs.async.PluginScope

abstract class Scoped<T> {
    protected open val scope: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
    abstract val value: T
    suspend fun <T> withScope(block: suspend CoroutineScope.() -> T) = withContext(scope) {
        block(this)
    }

    fun <T> launch(block: suspend CoroutineScope.() -> T) = PluginScope.launch(scope) {
        block(this)
    }
}