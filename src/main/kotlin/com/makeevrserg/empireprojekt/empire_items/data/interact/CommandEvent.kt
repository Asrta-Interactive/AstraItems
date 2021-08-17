package com.makeevrserg.empireprojekt.items.data.interact

import com.google.gson.annotations.SerializedName

data class CommandEvent(
    @SerializedName("command")
    val command:String,
    @SerializedName("as_console")
    val asConsole:Boolean
)
