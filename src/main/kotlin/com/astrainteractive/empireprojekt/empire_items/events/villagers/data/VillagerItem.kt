package com.astrainteractive.empireprojekt.empire_items.events.villagers.data

import com.google.gson.annotations.SerializedName

data class VillagerItem(
    @SerializedName("chance")
    val chance:Int,
    @SerializedName("min_level")
    val minLevel:Int,
    @SerializedName("max_level")
    val maxLevel:Int,
    @SerializedName("left_item")
    val leftItem: SlotItem,
    @SerializedName("middle_item")
    val middleItem:SlotItem?,
    @SerializedName("result_item")
    val resultItem:SlotItem
)
