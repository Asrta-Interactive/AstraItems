package com.makeevrserg.empireprojekt.empire_items.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

/**
 * Показывать игроку рецепт кастомных предметов
 */
class PlayerShowRecipeKey : IEmpireListener {
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
            val mainItemId = itemStack.getEmpireID() ?: itemStack.type.name
            val mainRecipe = EmpireUtils.getRecipeKey(mainItemId)
            if (mainRecipe != null && player.hasDiscoveredRecipe(mainRecipe))
                return@runAsyncTask

            for (id in EmpireUtils.useInCraft(mainItemId))
                callSyncMethod {
                    player.discoverRecipe(EmpireUtils.getRecipeKey(id) ?: return@callSyncMethod)
                }

            callSyncMethod {
                player.discoverRecipe(mainRecipe ?: return@callSyncMethod)
            }
        }
    }

    override fun onDisable() {
        EntityPickupItemEvent.getHandlerList().unregister(this)
        CraftItemEvent.getHandlerList().unregister(this)
    }

}