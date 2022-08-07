package com.astrainteractive.empire_items.empire_items.gui

import com.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.astralibs.utils.catching
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.models.GUI_CONFIG
import com.astrainteractive.empire_items.api.models.VillagerTradeInfo
import org.bukkit.ChatColor
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

class GuiCraftingViewModel(val playerMenuUtility: PlayerMenuUtility, val itemID: String) {
    /**
     * Предметы, которые можно сделать с помощью [itemID]
     */
    val recipes = listOf(CraftingApi.recipesMap[itemID])

    /**
     * Крафты, в которых [itemID] используется
     */
    val usedInCraftIDS = CraftingApi.usedInCraft(itemID)
    val usedInCraftItemStacks = usedInCraftIDS.map { it.toAstraItemOrItem() }
    private val drops = EmpireItemsAPI.dropsByID[itemID]
    private val generatedBlock = EmpireItemsAPI.itemYamlFilesByID[itemID]?.block?.generate
    private val villagerTrades = EmpireItemsAPI.villagerTradeInfoByID.values.mapNotNull {
        val filtered = it.trades.filter { it.value.id == itemID }.ifEmpty {
            return@mapNotNull null
        }
        VillagerTradeInfo(it.id, it.profession, filtered)
    }
    private var recipeInfoIndex = 0

    val dropInfo =
        if (drops.isNullOrEmpty()) null else GUI_CONFIG.settings.buttons.moreButton.toAstraItemOrItem()!!.apply {
            editMeta {
                it.setDisplayName((EmpirePlugin.translations.guiInfoDropColor + EmpirePlugin.translations.guiInfoDrop).HEX())
                it.lore =
                    drops.map { "${ChatColor.GRAY}${it.dropFrom}: [${it.minAmount};${it.maxAmount}] ${it.chance}%" }
            }
        }

    val blockInfo = generatedBlock?.let { b ->
        GUI_CONFIG.settings.buttons.moreButton.toAstraItemOrItem()!!.apply {
            editMeta {
                it.setDisplayName((EmpirePlugin.translations.guiInfoDropColor + "Генерируется:").HEX())
                it.lore = listOf(
                    ("${ChatColor.GRAY}На высоте [${b.minY};${b.maxY}]"),
                    ("${ChatColor.GRAY}Количество в чанке [${b.minPerChunk};${b.maxPerChunk}]"),
                    ("${ChatColor.GRAY}Количество в депозите [${b.minPerDeposit};${b.maxPerDeposit}]"),
                    ("${ChatColor.GRAY}В мире: ${b.world ?: "любом"}"),
                    ("${ChatColor.GRAY}С шансом: ${b.generateInChunkChance}%")
                )
            }
        }
    }

    val villagersInfo =
        if (villagerTrades.isEmpty()) null else GUI_CONFIG.settings.buttons.moreButton.toAstraItemOrItem()!!.apply {
            editMeta {
                it.setDisplayName((EmpirePlugin.translations.guiInfoDropColor + "Можно купить у жителя:").HEX())
                it.lore = villagerTrades.map { "${ChatColor.GRAY}${it.profession}" }
            }
        }
    private val recipesInfo = recipes.flatMap {
        it?.flatMap {
            listOfNotNull(
                (it as? ShapelessRecipe?)?.let(::fromShapelessRecipe),
                (it as? FurnaceRecipe?)?.let(::fromFurnaceRecipe),
                (it as? ShapedRecipe?)?.let(::fromShapedRecipe)
            )
        } ?: listOf()
    }


    fun onRecipeIndexChanged() {
        recipeInfoIndex++
    }

    val index: Int
        get() = catching { recipeInfoIndex % recipesInfo.size } ?: 0

    val currentRecipeInfo: RecipeInfo?
        get() = recipesInfo.getOrNull(index)


    enum class RecipeType {
        CRAFTING_TABLE, FURNACE
    }

    data class RecipeInfo(
        val amount: Int,
        val ingredients: List<ItemStack?>,
        val type: RecipeType
    )

    private fun ShapedRecipe.get(x: Int, y: Int): ItemStack? = ingredientMap[shape.getOrNull(x)?.getOrNull(y)]
    private fun fromShapedRecipe(it: ShapedRecipe): RecipeInfo {
        val range = IntRange(0, 2)
        val list = range.flatMap { x -> range.map { y -> it.get(x, y) } }
        return RecipeInfo(
            it.result.amount,
            list,
            RecipeType.CRAFTING_TABLE
        )

    }

    private fun fromFurnaceRecipe(it: FurnaceRecipe): RecipeInfo {
        val range = IntRange(0, 2)
        val list: MutableList<ItemStack?> = range.flatMap { x -> range.map { y -> null } }.toMutableList()
        list[4] = it.input
        return RecipeInfo(
            it.result.amount,
            list,
            RecipeType.FURNACE
        )
    }

    private fun fromShapelessRecipe(it: ShapelessRecipe): RecipeInfo {
        val range = IntRange(0, 8)
        val list: MutableList<ItemStack?> = range.map { x -> it.ingredientList.getOrNull(x) }.toMutableList()
        return RecipeInfo(
            it.result.amount,
            list,
            RecipeType.CRAFTING_TABLE
        )
    }
}