package com.astrainteractive.empireprojekt.empire_items.api.crafting

import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empireprojekt.empire_items.util.YamlParser
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapelessRecipe
import java.lang.IllegalStateException
import kotlin.math.max

data class AstraShapelessRecipe(
    val id: String,
    val input: String,
    val result: String,
    val amount: Int
) {


    fun createRecipe() {
        val namespacedKey = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING+id)
        val resultItem = result.toAstraItemOrItem(amount)?:return
        val inputItem = input.toAstraItemOrItem()?:return
        val recipeChoise =
            if (inputItem.getAstraID() != null) RecipeChoice.ExactChoice(inputItem) else RecipeChoice.MaterialChoice(
                inputItem.type
            )


        val shapelessRecipe = ShapelessRecipe(namespacedKey, resultItem)
        shapelessRecipe.addIngredient(recipeChoise)

        try {
            Bukkit.addRecipe(shapelessRecipe)
            ItemManager.addRecipe(result,shapelessRecipe)
        } catch (e: IllegalStateException) {
            Logger.log(this.javaClass.name,"Не удалось добавить крафт ${id} ${e.message}",logType = Logger.Type.WARN)
        }

    }

    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        fun getRecipe(s: ConfigurationSection?): AstraShapelessRecipe? {
            val parser = YamlParser.parser
            val res = parser.configurationSectionToClass<AstraShapelessRecipe>(s?:return null)?:return null
            val id = "s_" + parser.fixNull(res.id,s.name)
            return AstraShapelessRecipe(id = id, input = res.input, result = res.result, amount = max(1,res.amount))
        }
    }
}