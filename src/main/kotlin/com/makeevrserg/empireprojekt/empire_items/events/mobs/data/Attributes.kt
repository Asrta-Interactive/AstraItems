package com.makeevrserg.empireprojekt.empire_items.events.mobs.data

import com.google.gson.annotations.SerializedName

data class Attributes(
    @SerializedName("attribute")
    val attribute:String,
    @SerializedName("min")
    val min:String,
    @SerializedName("max")
    val max:Double
)