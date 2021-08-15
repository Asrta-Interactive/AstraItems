package com.makeevrserg.empireprojekt.events.mobs.data

import com.google.gson.annotations.SerializedName

data class MobSpawnReplace(
    @SerializedName("type")
    val type:String,
    @SerializedName("chance")
    val chance:Double
)