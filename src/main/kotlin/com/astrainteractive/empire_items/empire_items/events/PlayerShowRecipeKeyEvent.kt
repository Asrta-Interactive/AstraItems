package com.astrainteractive.empire_items.empire_items.events

import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.AsyncHelper.callSyncMethod
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

/**
 * Показывать игроку рецепт кастомных предметов
 */
class PlayerShowRecipeKeyEvent {
    /**
     * Когда игрок поднимает предмет - даём ему рецепты
     */
    val playerItemPickUp = DSLEvent.event(EntityPickupItemEvent::class.java)  { e ->
        val entity = e.entity
        if (entity !is Player)
            return@event
        val player = entity as Player
        val itemStack = e.item.itemStack
        addPlayerRecipe(itemStack, player)

    }

    /**
     * Когда игрок крафтит
     */
    val craftItemEvent = DSLEvent.event(CraftItemEvent::class.java)  { e ->
        val entity = e.whoClicked
        if (entity !is Player)
            return@event

        val player = entity as Player
        val itemStack = e.recipe.result ?: return@event
        addPlayerRecipe(itemStack, player)
    }

    /**
     * Добавить предмет в меню крафтинга
     */
    private fun addPlayerRecipe(
        itemStack: ItemStack,
        player: Player
    ) {
        AsyncHelper.launch {
            val mainItemId = itemStack.empireID ?: itemStack.type.name
            val ids = mutableListOf(mainItemId).apply { addAll(CraftingApi.usedInCraft(mainItemId)) }
            val toDiscover =
                ids.flatMap { CraftingApi.getKeysById(it) ?: listOf() }.filter { !player.hasDiscoveredRecipe(it) }
            callSyncMethod {
                toDiscover.forEach {
                    Logger.log("Player ${player.name} discovered recipe ${it}", "Crafting", consolePrint = false)
                    player.discoverRecipe(it)
                }
            }
        }
    }
}