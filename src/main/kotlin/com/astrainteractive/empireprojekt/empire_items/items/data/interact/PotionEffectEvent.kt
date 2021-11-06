package com.astrainteractive.empireprojekt.items.data.interact

import com.google.gson.annotations.SerializedName

data class PotionEffectEvent(
    @SerializedName("effect")
    val effect:String,
    @SerializedName("amplifier")
    val amplifier:Int,
    @SerializedName("duration")
    val duration:Int
)
