package com.makeevrserg.empireprojekt.items.data.decoration

import com.google.gson.annotations.SerializedName

data class Decoration(
    @SerializedName("small")
    val small:Boolean,
    @SerializedName("physics")
    val physics:Boolean
)