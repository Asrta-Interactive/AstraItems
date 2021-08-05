package com.makeevrserg.empireprojekt.events.villagers

import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireUtils
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import java.lang.IllegalArgumentException

class VillagerManager {

    data class VillagerTrades(
        val chance: Double,
        val minLevel: Int,
        val maxLevel: Int,
        val itemLeft: ItemStack,
        val itemRight: ItemStack?,
        val result: ItemStack
    ) {
        companion object {
            fun getItemStack(recipe: Map<*, *>?): ItemStack? {
                recipe ?: return null
                val itemID = (recipe.keys.elementAtOrNull(0) ?: return null) as String
                val itemStack = EmpireUtils.getItemStackByID(itemID)?.clone() ?: return null
                itemStack.amount = (recipe.values.elementAtOrNull(0) ?: return null) as Int
                return itemStack
            }
        }
    }

    private fun initTrades() {
        val conf = EmpirePlugin.empireFiles.villagerTrades.getConfig() ?: return
        val villagersMap = mutableMapOf<Villager.Profession, List<VillagerTrades>>()
        for (key in conf.getKeys(false)) {
            val sect = conf.getConfigurationSection(key) ?: continue
            val profession = try {
                Villager.Profession.valueOf(sect.getString("profession") ?: key)
            } catch (e: IllegalArgumentException) {
                println("${EmpirePlugin.translations.WRONG_ENUM} ${sect.getString("profession") ?: key}")
                continue
            }
            val tradesList = mutableListOf<VillagerTrades>()
            for (item in sect.getConfigurationSection("trades")?.getKeys(false) ?: continue) {
                val sect_ = sect.getConfigurationSection("trades")?.getConfigurationSection(item) ?: continue
                val recipes = sect_.getMapList("recipes")
                val itemLeft = VillagerTrades.getItemStack(recipes.elementAtOrNull(0))?.clone()
                val itemRight =
                    if (recipes.size == 3) VillagerTrades.getItemStack(recipes.elementAtOrNull(1))?.clone() else null
                val itemResult = VillagerTrades.getItemStack(recipes.elementAtOrNull(recipes.size - 1))?.clone()

                if (itemLeft == null) {
                    println(EmpirePlugin.translations.ITEM_NOT_FOUND + recipes.elementAtOrNull(0))
                    continue
                }
                if (itemRight == null && recipes.size == 3) {
                    println(EmpirePlugin.translations.ITEM_NOT_FOUND + recipes.elementAtOrNull(1))
                    continue
                }
                if (itemResult == null) {
                    println(EmpirePlugin.translations.ITEM_NOT_FOUND + recipes.elementAtOrNull(recipes.size - 1))
                    continue
                }
                val trade = VillagerTrades(
                    sect_.getDouble("chance"),
                    sect_.getInt("min_level", 1),
                    sect_.getInt("max_levelt", 5),
                    itemLeft,
                    itemRight,
                    itemResult
                )
                tradesList.add(
                    trade
                )
                addVillagerByItem(profession.name, EmpireUtils.getEmpireID(itemResult))
            }


            villagersMap[profession] = tradesList

        }
        villagerTradeByProfession = villagersMap
    }

    private fun addVillagerByItem(villager: String, item: String?) {
        item ?: return
        if (!villagerByItem.containsKey(item))
            villagerByItem[item] = mutableListOf<String>(villager)
        else if (!villagerByItem[item]!!.contains(villager))
            villagerByItem[item]?.add(villager)
    }

    companion object {

        lateinit var villagerTradeByProfession: Map<Villager.Profession, List<VillagerTrades>>
        var villagerByItem: MutableMap<String, MutableList<String>> = mutableMapOf()
    }

    init {

        initTrades()
    }
}