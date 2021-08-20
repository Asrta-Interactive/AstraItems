package com.makeevrserg.empireprojekt.empire_items.events.villagers.data

import com.google.gson.annotations.SerializedName

data class SlotItem(
    @SerializedName("id")
    val id:String,
    @SerializedName("amount")
    val amount:Int
)
