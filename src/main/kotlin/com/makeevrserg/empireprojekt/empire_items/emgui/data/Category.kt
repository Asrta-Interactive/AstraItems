package makeevrserg.empireprojekt.emgui.data

import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser

data class Category(
    val id: String,
    val title: String,
    val name: String,
    val icon: String,
    val lore: List<String>,
    val permission: String,
    val items: List<String>
) {
    companion object {

        fun toMap(list:List<Category>): Map<String, Category> {
            val map = mutableMapOf<String,Category>()
            for (category in list) {
                map[category.id] = category
            }
            return map
        }

        fun newCategories(): List<Category>? {
            return EmpireYamlParser.fromYAML<List<Category>>(
                EmpirePlugin.empireFiles.guiFile.getConfig(),
                object : TypeToken<List<Category?>?>() {}.type,
                listOf("categories")
            )
        }
    }
}