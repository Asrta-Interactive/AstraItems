package com.astrainteractive.empire_items.empire_items.util

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private interface AsyncTask : CoroutineScope {
    private val job: Job
        get() = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}
object AsyncHelper:AsyncTask{
    inline fun <T>runBackground(scope: CoroutineDispatcher=Dispatchers.IO, crossinline block:()->T){
        launch(scope) {
            block.invoke()
        }
    }
}