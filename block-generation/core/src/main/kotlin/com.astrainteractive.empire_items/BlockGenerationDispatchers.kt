package com.astrainteractive.empire_items

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext

object BlockGenerationDispatchers {
    val fileHistoryScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))
    val blockGenerationPool = Dispatchers.IO.limitedParallelism(4.coerceAtLeast(Runtime.getRuntime().availableProcessors()))
    val blockParsingPool = Dispatchers.IO.limitedParallelism(4.coerceAtLeast(Runtime.getRuntime().availableProcessors()))
    val generatorLauncherDispatcher = Dispatchers.IO.limitedParallelism(1)
}