package com.astrainteractive.empireprojekt.empire_items.events

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.callSyncMethod
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.empire_items.api.crafting.CraftingManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

/**
 * Показывать игроку рецепт кастомных предметов
 */
class PlayerShowRecipeKey : IAstraListener {
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
        val itemStack = e.currentItem ?: return
        addPlayerRecipe(itemStack, player)
    }

    /**
     * Добавить предмет в меню крафтинга
     */
    private fun addPlayerRecipe(
        itemStack: ItemStack,
        player: Player
    ) {
        runAsyncTask {
            val mainItemId = itemStack.getAstraID() ?: itemStack.type.name
            val mainRecipeKey = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING+ mainItemId)
            if (player.hasDiscoveredRecipe(mainRecipeKey))
                return@runAsyncTask

            for (id in CraftingManager.usedInCraft(mainItemId))
                callSyncMethod {
                    val key = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING+ id)
                    player.discoverRecipe(key)
                }

            callSyncMethod {
                player.discoverRecipe(mainRecipeKey)
            }
        }
    }

    override fun onDisable() {
        EntityPickupItemEvent.getHandlerList().unregister(this)
        CraftItemEvent.getHandlerList().unregister(this)
    }

}