package com.astrainteractive.empire_items.empire_items.api.items.data.decoration

import com.google.gson.annotations.SerializedName

data class Decoration(
    @SerializedName("small")
    val small:Boolean,
    @SerializedName("physics")
    val physics:Boolean
)