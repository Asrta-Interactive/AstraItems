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
import java.lang.IllegalStateException
import kotlin.math.max

data class AstraFurnaceRecipe(
    val id: String,
    val result: String,
    val input: String,
    val cookTime: Int,
    val exp: Int,
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
        val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
            namespacedKey,
            resultItem,
            recipeChoise,
            exp.toFloat(),
            cookTime
        )
        try {
            Bukkit.addRecipe(furnaceRecipe)
            ItemManager.addRecipe(result,furnaceRecipe)
        } catch (e: IllegalStateException) {
            Logger.log(this.javaClass.name,"Не удалось добавить крафт ${id} ${e.message}",logType = Logger.Type.WARN)
        }

    }
    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        private fun getRecipe(s: ConfigurationSection?): AstraFurnaceRecipe? {
            val parser = YamlParser.parser
            val res = parser.configurationSectionToClass<AstraFurnaceRecipe>(s?:return null)?:return null
            val id = "f_" + parser.fixNull(res.id,s.name)
            return AstraFurnaceRecipe(
                id = id,
                input = res.input,
                result = res.result,
                cookTime = max(20,res.cookTime),
                exp = max(1,res.exp),
                amount = max(res.amount,1)
            )
        }
    }
}