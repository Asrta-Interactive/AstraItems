package com.makeevrserg.empireprojekt.events.villagers

import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireUtils

import org.bukkit.ChatColor
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.event.entity.VillagerReplenishTradeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import java.lang.IllegalArgumentException
import kotlin.random.Random

class VillagerEvent : Listener {


    private fun addRecipe(villager: Villager, trade: VillagerTrades) {

        if (villager.villagerLevel < trade.minLevel)
            return
        if (villager.villagerLevel > trade.maxLevel)
            return
        if (trade.chance < Random.nextDouble(100.0))
            return
        val mRecipe = MerchantRecipe(trade.result, 1)
        mRecipe.addIngredient(trade.itemLeft)
        mRecipe.maxUses = Random.nextInt(1,6)
        if (trade.itemRight != null)
            mRecipe.addIngredient(trade.itemRight)
        if (villager.recipes.contains(mRecipe))
            return
        for (vRecipe in villager.recipes)
            if (vRecipe.ingredients == mRecipe.ingredients && vRecipe.result==mRecipe.result)
                return

        val recipes = villager.recipes.toMutableList()
        recipes.add(mRecipe)
        villager.recipes = recipes
    }


    @EventHandler
    fun villagerAcquireTradeEvent(e: VillagerAcquireTradeEvent) {
        println(ChatColor.AQUA.toString() + e.eventName)
        val villager = e.entity as Villager
        val trades = villagerTradeByProfession[villager.profession] ?: return
        if (e.isCancelled)
            return
        for (trade in trades)
            addRecipe(villager, trade)


    }


    @EventHandler
    fun villager2(e: VillagerReplenishTradeEvent) {
        println(ChatColor.RED.toString() + e.eventName)
    }

    @EventHandler
    fun villager2(e: VillagerCareerChangeEvent) {
        println(ChatColor.RED.toString() + e.eventName)

    }

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

    lateinit var villagerTradeByProfession: Map<Villager.Profession, List<VillagerTrades>>

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
                val itemRight = if (recipes.size==3) VillagerTrades.getItemStack(recipes.elementAtOrNull(1))?.clone() else null
                val itemResult = VillagerTrades.getItemStack(recipes.elementAtOrNull(recipes.size-1))?.clone()

                if (itemLeft==null){
                    println(EmpirePlugin.translations.ITEM_NOT_FOUND+recipes.elementAtOrNull(0))
                    continue
                }
                if (itemRight==null && recipes.size==3){
                    println(EmpirePlugin.translations.ITEM_NOT_FOUND+recipes.elementAtOrNull(1))
                    continue
                }
                if (itemResult==null){
                    println(EmpirePlugin.translations.ITEM_NOT_FOUND+recipes.elementAtOrNull(recipes.size-1))
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
            }

            //println("Map=${profession.name} items=${tradesList}")
            villagersMap[profession] = tradesList

        }
        villagerTradeByProfession = villagersMap
    }

    init {
        initTrades()
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)

    }


    fun onDisable() {
        VillagerAcquireTradeEvent.getHandlerList().unregister(this)
        VillagerReplenishTradeEvent.getHandlerList().unregister(this)
        VillagerCareerChangeEvent.getHandlerList().unregister(this)
    }
}