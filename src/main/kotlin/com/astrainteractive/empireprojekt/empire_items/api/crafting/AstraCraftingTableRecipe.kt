package com.astrainteractive.empireprojekt.empire_items.api.crafting

import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.empireprojekt.empire_items.api.utils.getPersistentData
import com.astrainteractive.empireprojekt.empire_items.util.YamlParser
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.yaml.snakeyaml.Yaml
import java.lang.reflect.Type
import kotlin.math.max

data class AstraCraftingTableRecipe(
    val id: String,
    val result: String,
    val amount: Int,
    val pattern: List<String>?,
    val ingredients: Map<Char, String>?
) {
    fun createRecipe() {
        val namespacedKey = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING + id)
        val shapedRecipe = ShapedRecipe(namespacedKey, result.toAstraItemOrItem(amount) ?: return)
        pattern ?: return
        if (pattern.size == 3)
            shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
        else if (pattern.size == 2)
            shapedRecipe.shape(pattern[0], pattern[1])
        ingredients?.forEach { (ch, item) ->
            val itemStack = item.toAstraItemOrItem() ?: return@forEach
            val choice: RecipeChoice =
                if (itemStack.getAstraID() != null && itemStack.itemMeta?.getPersistentData(BukkitConstants.CRAFT_DURABILITY) == null)
                    RecipeChoice.ExactChoice(itemStack)
                else
                    RecipeChoice.MaterialChoice(itemStack.type)


            if (ch.equals('x', ignoreCase = true))
                shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
            else
                shapedRecipe.setIngredient(ch, choice)
        }
        try {
            Bukkit.addRecipe(shapedRecipe)
            ItemManager.addRecipe(result, shapedRecipe)
        } catch (e: IllegalStateException) {
            Logger.log(this.javaClass.name, "Не удалось добавить крафт ${id} ${e.message}", logType = Logger.Type.WARN)
        }
    }


    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        private fun getRecipe(s: ConfigurationSection?): AstraCraftingTableRecipe? {
            val parser = YamlParser()
            val res = parser.configurationSectionToClass<AstraCraftingTableRecipe>(s?:return null)?:return null
            val id = "ct_"+parser.fixNull(res.id,s.name)
            return AstraCraftingTableRecipe(
                id = id,
                result = parser.fixNull(res.result,id),
                pattern = res.pattern,
                amount = max(res.amount,1),
                ingredients = res.ingredients
            )
        }
    }
}


