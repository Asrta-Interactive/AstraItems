package com.makeevrserg.empireprojekt.empire_items.util.crafting.data

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser

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