package com.astrainteractive.empire_items.empire_items.events

import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.AsyncHelper.callSyncMethod
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

/**
 * Показывать игроку рецепт кастомных предметов
 */
class PlayerShowRecipeKeyEvent : EventListener {
    /**
     * Когда игрок поднимает предмет - даём ему рецепты
     */
    @EventHandler
    fun playerItemPickUp(e: EntityPickupItemEvent) {
        val entity = e.entity
        if (entity !is Player)
            return
        val player = entity as Player
        val itemStack = e.item.itemStack
        addPlayerRecipe(itemStack, player)

    }

    /**
     * Когда игрок крафтит
     */
    @EventHandler
    fun craftItemEvent(e: CraftItemEvent) {
        val entity = e.whoClicked
        if (entity !is Player)
            return

        val player = entity as Player
        val itemStack = e.recipe.result ?: return
        addPlayerRecipe(itemStack, player)
    }

    /**
     * Добавить предмет в меню крафтинга
     */
    private fun addPlayerRecipe(
        itemStack: ItemStack,
        player: Player
    ) {
        AsyncHelper.runBackground {
            val mainItemId = itemStack.getAstraID() ?: itemStack.type.name
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

    override fun onDisable() {
        EntityPickupItemEvent.getHandlerList().unregister(this)
        CraftItemEvent.getHandlerList().unregister(this)
    }

}