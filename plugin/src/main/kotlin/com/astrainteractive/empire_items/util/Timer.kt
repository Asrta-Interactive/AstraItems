package com.astrainteractive.empire_items.util

class Timer {
    private var started = System.currentTimeMillis()
    fun start(): Timer {
        started = System.currentTimeMillis()
        return this
    }
    fun isEnded(time:Long=1000L) = (System.currentTimeMillis()-started)>time

    fun stop(time:Int=1000): Double {
        return (System.currentTimeMillis() - started) / time.toDouble()
    }

    fun calculate(time: Int=1000,function: () -> Unit): Double {
        start()
        function.invoke()
        return stop(time)
    }

}