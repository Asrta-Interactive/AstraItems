package com.makeevrserg.empireprojekt.empire_items.util.crafting.data

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser

data class EmpireCraftingTableRecipe(
    @SerializedName("result")
    var result:String?,
    @SerializedName("pattern")
    val pattern:List<String>,
    @SerializedName("ingredients")
    val ingredients:Map<Char,String>,
    @SerializedName("amount")
    val amount:Int?
){
    companion object{
        fun new(): List<EmpireCraftingTableRecipe>  = EmpireYamlParser.fromYAML<List<EmpireCraftingTableRecipe>>(
            EmpirePlugin.empireFiles.craftingFile.getConfig(),
            object : TypeToken<List<EmpireCraftingTableRecipe?>?>() {}.type,
            listOf("crafting_table")
        )?: mutableListOf()
    }
}
