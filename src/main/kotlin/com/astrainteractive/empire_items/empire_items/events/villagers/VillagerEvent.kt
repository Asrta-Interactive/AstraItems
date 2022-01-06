package com.astrainteractive.empire_items.empire_items.events.villagers

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.empire_items.empire_items.api.v_trades.TradeItem
import com.astrainteractive.empire_items.empire_items.api.v_trades.VillagerTradeManager

import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.event.entity.VillagerReplenishTradeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.random.Random

class VillagerEvent : IAstraListener {


    /**
     * У жителя создается трейд
     */
    @EventHandler
    fun villagerAcquireTradeEvent(e: VillagerAcquireTradeEvent) {
        if (e.entity !is Villager)
            return
        val villager = e.entity as Villager
        val trades = VillagerTradeManager.villagerTradeByProfession(villager.profession.name) ?: return
        if (e.isCancelled)
            return
        for (trade in trades.trades)
            addRecipe(villager, trade)


    }


    /**
     * Замена предмета на аналогичный
     */
    private fun replaceEmpireItem(itemStack: ItemStack?): ItemStack? =
        itemStack?.getAstraID()?.toAstraItemOrItem()?.apply {
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
        val trades = VillagerTradeManager.villagerTradeByProfession(villager.profession.name) ?: return

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
    private fun addRecipe(villager: Villager, trade: TradeItem) {

        if (villager.villagerLevel < trade.minLevel)
            return
        if (villager.villagerLevel > trade.maxLevel)
            return
        if (trade.chance < Random.nextDouble(100.0))
            return

        val mRecipe =
            MerchantRecipe(trade.id.toAstraItemOrItem()?.clone()?.apply { amount = trade.amount }
                ?: return, Random.nextInt(trade.minUses, trade.maxUses))
        mRecipe.maxUses = Random.nextInt(trade.minUses, trade.maxUses)
        println("MaxUses of ${trade.id} ${mRecipe.maxUses}")
        mRecipe.addIngredient(trade.leftItem.id.toAstraItemOrItem()?.clone()?.apply {
            amount = trade.leftItem.amount }
            ?: return)
        if (trade.middleItem != null)
            mRecipe.addIngredient(
                trade.middleItem.id.toAstraItemOrItem()?.clone()?.apply { amount = trade.middleItem.amount } ?: return)
        if (villager.recipes.contains(mRecipe))
            return
        for (vRecipe in villager.recipes)
            if (vRecipe.ingredients == mRecipe.ingredients && vRecipe.result == mRecipe.result)
                return

        val recipes = villager.recipes.toMutableList()
        recipes.add(mRecipe)
        villager.recipes = recipes
    }
    override fun onDisable() {
        VillagerAcquireTradeEvent.getHandlerList().unregister(this)
        VillagerReplenishTradeEvent.getHandlerList().unregister(this)
        VillagerCareerChangeEvent.getHandlerList().unregister(this)
        PlayerInteractEntityEvent.getHandlerList().unregister(this)
    }
}