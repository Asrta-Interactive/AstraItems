package com.astrainteractive.empire_items.api.hud.thirst

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.AsyncTask
import java.util.*

class RepeatableTask constructor(delay:Long,block: () -> Unit) : AsyncTask {
    var timer:Timer = kotlin.concurrent.timer(UUID.randomUUID().toString(),daemon = true,0L,delay) {
        block.invoke()
    }

    init {
        addTask(this)
    }

    fun cancel() {
        timer.cancel()
    }

    companion object {
        private val lock: Any = Any()
        private val tasks: MutableList<RepeatableTask> = mutableListOf()
        fun addTask(task: RepeatableTask) = synchronized(lock) {
            tasks.add(task)
        }

        fun removeTask(task: RepeatableTask) = synchronized(lock) {
            task.cancel()
            tasks.remove(task)
        }

        fun clearTasks() = synchronized(lock) {
            tasks.forEach { it.cancel() }
            tasks.clear()
        }

        fun cancel() {
            clearTasks()
        }
    }
}