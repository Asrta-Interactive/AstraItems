package com.astrainteractive.empire_items.empire_items.util

class Timer {
    private var started = System.currentTimeMillis()
    fun start(): Timer {
        started = System.currentTimeMillis()
        return this
    }

    fun stop(): Double {
        return (System.currentTimeMillis() - started) / 1000.0
    }

}