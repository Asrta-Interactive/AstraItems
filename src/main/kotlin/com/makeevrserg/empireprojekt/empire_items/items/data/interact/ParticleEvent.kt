package com.makeevrserg.empireprojekt.items.data.interact

import com.google.gson.annotations.SerializedName

data class ParticleEvent(
    @SerializedName("name")
    val name:String,
    @SerializedName("count")
    val count:Int,
    @SerializedName("time")
    val time:Double
)
