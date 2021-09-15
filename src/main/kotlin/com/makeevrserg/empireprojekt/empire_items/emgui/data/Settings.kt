package com.makeevrserg.empireprojekt.empire_items.emgui.data

import com.google.gson.annotations.SerializedName
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import org.bukkit.Material

data class Settings(
    @SerializedName("workbench_ui")
    val workbenchUi:String="",
    @SerializedName("categories_text")
    val categoriesText:String="",
    @SerializedName("sounds_text")
    val soundsText:String="",
    @SerializedName("drop_btn")
    val dropButton:String=Material.BARRIER.name,
    @SerializedName("next_btn")
    val nextButton:String=Material.PAPER.name,
    @SerializedName("prev_btn")
    val prevButton:String=Material.PAPER.name,
    @SerializedName("back_btn")
    val backButton:String=Material.BARRIER.name,
    @SerializedName("close_btn")
    val closeButton:String= Material.BARRIER.name,
    @SerializedName("give_btn")
    val giveButton:String=Material.DIAMOND.name,
    @SerializedName("furnace_btn")
    val furnaceButton:String=Material.FURNACE.name,
    @SerializedName("crafting_table_btn")
    val craftingTableButton:String=Material.CRAFTING_TABLE.name,
    @SerializedName("workbench_sound")
    val workbenchSound:String="",
    @SerializedName("categories_sound")
    val categoriesSound:String="",
    @SerializedName("category_sound")
    val categorySound:String=""
){
    companion object{
        fun new(): Settings {

            return EmpireYamlParser.fromYAML<Settings>(
                EmpirePlugin.empireFiles.guiFile.getConfig(),
                Settings::class.java,
                listOf("settings")
            )?: Settings()
        }
    }
}
