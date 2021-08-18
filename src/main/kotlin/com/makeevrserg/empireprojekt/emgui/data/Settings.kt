package makeevrserg.empireprojekt.emgui.data

import com.google.gson.annotations.SerializedName
import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireYamlParser

data class Settings(
    @SerializedName("workbench_ui")
    val workbenchUi:String,
    @SerializedName("categories_text")
    val categoriesText:String,
    @SerializedName("sounds_text")
    val soundsText:String,
    @SerializedName("drop_btn")
    val dropButton:String,
    @SerializedName("next_btn")
    val nextButton:String,
    @SerializedName("prev_btn")
    val prevButton:String,
    @SerializedName("back_btn")
    val backButton:String,
    @SerializedName("close_btn")
    val closeButton:String,
    @SerializedName("give_btn")
    val giveButton:String,
    @SerializedName("furnace_btn")
    val furnaceButton:String,
    @SerializedName("crafting_table_btn")
    val craftingTableButton:String,
    @SerializedName("workbench_sound")
    val workbenchSound:String,
    @SerializedName("categories_sound")
    val categoriesSound:String,
    @SerializedName("category_sound")
    val categorySound:String
){
    companion object{
        fun new(): Settings {

            return EmpireYamlParser.fromYAML<Settings>(
                EmpirePlugin.empireFiles.guiFile.getConfig(),
                Settings::class.java,
                listOf("settings")
            )!!
        }
    }
}
