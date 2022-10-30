package com.astrainteractive.empire_items.util

import ru.astrainteractive.astralibs.async.PluginScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CleanerTask(val period: Long, val task: () -> Unit) {
    init {
        PluginScope.launch(Dispatchers.IO) {
            while (true) {
                delay(period)
                task()
                if (!this.isActive) break
            }
        }
    }
}