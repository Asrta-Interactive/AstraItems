package com.astrainteractive.empire_items.events.genericevents

import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_items.util.ext_api.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.empireID
import com.atrainteractive.empire_items.models.VillagerTradeInfo
import org.bukkit.entity.Villager
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import kotlin.random.Random

class VillagerEvent() {
    private val empireItemsAPI by empireItemsApiModule


    /**
     * У жителя создается трейд
     */
    val villagerAcquireTradeEvent = DSLEvent.event<VillagerAcquireTradeEvent> { e ->
        if (e.entity !is Villager)
            return@event
        val villager = e.entity as Villager
        val trades = empireItemsAPI.villagerTradeInfoByProfession[villager.profession.name] ?: return@event
        if (e.isCancelled)
            return@event
        for (trade in trades.flatMap { it.trades.values })
            addRecipe(villager, trade)


    }


    /**
     * Замена предмета на аналогичный
     */
    private fun replaceEmpireItem(itemStack: ItemStack?): ItemStack? =
        itemStack?.empireID?.toAstraItemOrItem(itemStack.amount) ?: itemStack


    /**
     * Эвент взаимодействия с жителем - необходим для замены предметов
     */
    val villagerInteractEvent = DSLEvent.event<PlayerInteractEntityEvent> { e ->
        if (e.rightClicked !is Villager)
            return@event
        if (!e.player.isSneaking)
            return@event

        val villager = e.rightClicked as Villager
        val trades = empireItemsAPI.villagerTradeInfoByProfession[villager.profession.name] ?: return@event

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
    private fun addRecipe(villager: Villager, trade: VillagerTradeInfo.VillagerTrade) {

        if (villager.villagerLevel < trade.minLevel)
            return
        if (villager.villagerLevel > trade.maxLevel)
            return
        if (trade.chance < Random.nextDouble(100.0))
            return

        val mRecipe =
            MerchantRecipe(trade.id.toAstraItemOrItem()?.clone()?.apply { amount = trade.amount }
                ?: return, Random.nextInt(trade.minUses, trade.maxUses+1))
        mRecipe.maxUses = Random.nextInt(trade.minUses, trade.maxUses+1)
        mRecipe.addIngredient(trade.leftItem.id.toAstraItemOrItem()?.clone()?.apply {
            amount = trade.leftItem.amount
        }
            ?: return)
        if (trade.middleItem != null)
            mRecipe.addIngredient(
                trade.middleItem!!.id.toAstraItemOrItem()?.clone()?.apply { amount = trade.middleItem!!.amount } ?: return)
        if (villager.recipes.contains(mRecipe))
            return
        for (vRecipe in villager.recipes)
            if (vRecipe.ingredients == mRecipe.ingredients && vRecipe.result == mRecipe.result)
                return

        val recipes = villager.recipes.toMutableList()
        recipes.add(mRecipe)
        villager.recipes = recipes
    }
}