package com.makeevrserg.empireprojekt.empire_items.events.villagers

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.asEmpireItem
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.asEmpireItemOrItem
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.getEmpireID
import com.makeevrserg.empireprojekt.empire_items.events.villagers.data.VillagerItem
import com.makeevrserg.empireprojekt.empirelibs.*

import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.event.entity.VillagerReplenishTradeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.random.Random

class VillagerEvent : IEmpireListener {


    /**
     * У жителя создается трейд
     */
    @EventHandler
    fun villagerAcquireTradeEvent(e: VillagerAcquireTradeEvent) {
        if (e.entity !is Villager)
            return
        val villager = e.entity as Villager
        val trades = VillagerManager.villagerTradeByProfession[villager.profession.name] ?: return
        if (e.isCancelled)
            return
        for (trade in trades)
            addRecipe(villager, trade)


    }


    /**
     * Замена предмета на аналогичный
     */
    private fun replaceEmpireItem(itemStack: ItemStack?): ItemStack? =
        itemStack?.getEmpireID()?.asEmpireItem()?.clone()?.apply {
            amount = itemStack.amount
        } ?: itemStack


    /**
     * Эвент взаимодействия с жителем - необходим для замены предметов
     */
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


    /**
     * Добавление трейда к жителю
     */
    private fun addRecipe(villager: Villager, trade: VillagerItem) {

        if (villager.villagerLevel < trade.minLevel)
            return
        if (villager.villagerLevel > trade.maxLevel)
            return
        if (trade.chance < Random.nextDouble(100.0))
            return

        val mRecipe =
            MerchantRecipe(trade.resultItem.id.asEmpireItemOrItem()?.clone()?.apply { amount = trade.resultItem.amount }
                ?: return, 50)
        mRecipe.addIngredient(trade.leftItem.id.asEmpireItemOrItem()?.clone()?.apply {
            amount = trade.leftItem.amount }
            ?: return)
        mRecipe.maxUses = Random.nextInt(1, 6)
        if (trade.middleItem != null)
            mRecipe.addIngredient(
                trade.middleItem.id.asEmpireItemOrItem()?.clone()?.apply { amount = trade.middleItem.amount } ?: return)
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