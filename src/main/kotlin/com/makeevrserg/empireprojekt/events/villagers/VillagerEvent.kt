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


    @EventHandler
    fun villagerAcquireTradeEvent(e: VillagerAcquireTradeEvent) {
        println(ChatColor.AQUA.toString() + e.eventName)
        val villager = e.entity as Villager
        val trades = VillagerManager.villagerTradeByProfession[villager.profession] ?: return
        if (e.isCancelled)
            return
        for (trade in trades)
            addRecipe(villager, trade)


    }





    private fun addRecipe(villager: Villager, trade: VillagerManager.VillagerTrades) {

        if (villager.villagerLevel < trade.minLevel)
            return
        if (villager.villagerLevel > trade.maxLevel)
            return
        if (trade.chance < Random.nextDouble(100.0))
            return
        val mRecipe = MerchantRecipe(trade.result, 1)
        mRecipe.addIngredient(trade.itemLeft)
        mRecipe.maxUses = Random.nextInt(1, 6)
        if (trade.itemRight != null)
            mRecipe.addIngredient(trade.itemRight)
        if (villager.recipes.contains(mRecipe))
            return
        for (vRecipe in villager.recipes)
            if (vRecipe.ingredients == mRecipe.ingredients && vRecipe.result == mRecipe.result)
                return

        val recipes = villager.recipes.toMutableList()
        recipes.add(mRecipe)
        villager.recipes = recipes
    }




    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
        VillagerManager()

    }


    fun onDisable() {
        VillagerAcquireTradeEvent.getHandlerList().unregister(this)
        VillagerReplenishTradeEvent.getHandlerList().unregister(this)
        VillagerCareerChangeEvent.getHandlerList().unregister(this)
    }
}