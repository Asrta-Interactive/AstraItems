package com.makeevrserg.empireprojekt.empirelibs

object ETimer {
    private val timers = mutableMapOf<String,Long>()
    fun startTimer(key:String){
        timers[key] = System.nanoTime()
    }
    fun endTimer(key:String){
        println("Task $key enden in ${(System.nanoTime().minus(timers[key]?:return))/10000.0}")
        timers.remove(key)
    }
    fun timer(key: String){
        if (timers.containsKey(key))
            endTimer(key)
        else
            startTimer(key)
    }
}