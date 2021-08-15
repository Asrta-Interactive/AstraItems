package com.makeevrserg.empireprojekt.items.data.interact

import com.google.gson.annotations.SerializedName

data class Sound(
    @SerializedName("name")
    val song:String,
    @SerializedName("volume")
    val volume:Float?,
    @SerializedName("pitch")
    val pitch:Float?
)