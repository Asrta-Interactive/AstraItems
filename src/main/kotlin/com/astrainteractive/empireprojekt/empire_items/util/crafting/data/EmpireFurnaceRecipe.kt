package com.astrainteractive.empireprojekt.empire_items.util.crafting.data

import com.google.gson.annotations.SerializedName

data class EmpireFurnaceRecipe(
    @SerializedName("result")
    var result:String?,
    @SerializedName("input")
    val input:String,
    @SerializedName("cook_time")
    val cookTime:Int,
    @SerializedName("exp")
    val exp:Float,
    @SerializedName("amount")
    val amount:Int?
)