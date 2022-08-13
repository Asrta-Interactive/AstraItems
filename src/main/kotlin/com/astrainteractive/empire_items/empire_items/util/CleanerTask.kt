package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.async.AsyncHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CleanerTask(val period: Long, val task: () -> Unit) {
    fun start() = AsyncHelper.launch(Dispatchers.IO) {
        while (true) {
            delay(period)
            task()
            if (!this.isActive) break
        }
    }
}