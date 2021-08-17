package com.makeevrserg.empireprojekt.events.villagers

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.villagers.data.VillagerItem
import com.makeevrserg.empireprojekt.events.villagers.data.VillagerTrade
import empirelibs.EmpireUtils
import empirelibs.IEmpireListener
import empirelibs.getEmpireID
import empirelibs.getEmpireItem

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.event.entity.VillagerReplenishTradeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import java.lang.IllegalArgumentException
import kotlin.random.Random

class VillagerEvent : IEmpireListener {


    @EventHandler
    fun villagerAcquireTradeEvent(e: VillagerAcquireTradeEvent) {
        val villager = e.entity as Villager
        val trades = VillagerManager.villagerTradeByProfession[villager.profession.name] ?: return
        if (e.isCancelled)
            return
        for (trade in trades)
            addRecipe(villager, trade)


    }


    private fun checkForEquality(vItem: ItemStack?, tItem: ItemStack?): ItemStack? {
        vItem ?: return vItem
        if (vItem.getEmpireID().equals(tItem.getEmpireID()))
            if (vItem.amount == tItem?.amount)
                return tItem
        return vItem
    }


    private fun replaceEmpireItem(itemStack: ItemStack?): ItemStack? {
        val id = itemStack?.getEmpireID() ?: return itemStack
        val amount = itemStack.amount
        val newItemStack = EmpirePlugin.empireItems.empireItems[id]?.clone() ?: return itemStack
        newItemStack.amount = amount
        return newItemStack

    }

    @EventHandler
    fun villagerInteractEvent(e: PlayerInteractEntityEvent) {
        if (e.rightClicked !is Villager)
            return
        if (!e.player.isSneaking)
            return

        val villager = e.rightClicked as Villager
        val trades = VillagerManager.villagerTradeByProfession[villager.profession.name] ?: return

        val recipes = villager.recipes.toMutableList()
        villager.recipes = mutableListOf()
        for (i in recipes.indices) {


            val recipe = recipes[i]
            val ingredients = recipe.ingredients.toMutableList()
            recipe.ingredients = mutableListOf()
            if (ingredients.isEmpty())
                continue
            ingredients[0] = replaceEmpireItem(ingredients[0])
            if (ingredients.size == 2)
                ingredients[1] = replaceEmpireItem(ingredients[1])
            recipe.ingredients = ingredients
        }
        villager.recipes = recipes


    }


    private fun addRecipe(villager: Villager, trade: VillagerItem) {

        if (villager.villagerLevel < trade.minLevel)
            return
        if (villager.villagerLevel > trade.maxLevel)
            return
        if (trade.chance < Random.nextDouble(100.0))
            return
        val mRecipe = MerchantRecipe(trade.resultItem.id.getEmpireItem() ?: return, 1)
        mRecipe.addIngredient(trade.leftItem.id.getEmpireItem() ?: return)
        mRecipe.maxUses = Random.nextInt(1, 6)
        if (trade.middleItem != null)
            mRecipe.addIngredient(trade.middleItem.id.getEmpireItem() ?: return)
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
        VillagerManager()
    }


    override fun onDisable() {
        VillagerAcquireTradeEvent.getHandlerList().unregister(this)
        VillagerReplenishTradeEvent.getHandlerList().unregister(this)
        VillagerCareerChangeEvent.getHandlerList().unregister(this)
        PlayerInteractEntityEvent.getHandlerList().unregister(this)
    }
}