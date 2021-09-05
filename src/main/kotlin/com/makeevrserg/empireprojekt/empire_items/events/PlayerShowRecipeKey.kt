package com.makeevrserg.empireprojekt.empire_items.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.getEmpireID
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

class PlayerShowRecipeKey : IEmpireListener {
    @EventHandler
    fun playerItemPickUp(e: EntityPickupItemEvent) {
        val entity = e.entity
        if (entity !is Player)
            return
        val player = entity as Player
        val itemStack = e.item.itemStack
        addPlayerRecipe(itemStack, player)
    }

    @EventHandler
    fun craftItemEvent(e: CraftItemEvent) {
        val entity = e.whoClicked
        if (entity !is Player)
            return
        val player = entity as Player
        val itemStack = e.currentItem ?: return
        addPlayerRecipe(itemStack, player)
    }

    private fun addPlayerRecipe(
        itemStack: ItemStack,
        player: Player
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
            val mainItemId = itemStack.getEmpireID() ?: itemStack.type.name
            val mainRecipe = EmpireUtils.getRecipeKey(mainItemId)
            if (mainRecipe!=null && player.hasDiscoveredRecipe(mainRecipe))
                return@Runnable

            for (id in EmpireUtils.useInCraft(mainItemId))
                Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
                    player.discoverRecipe(EmpireUtils.getRecipeKey(id) ?: return@callSyncMethod)
                }


            Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
                player.discoverRecipe(mainRecipe?:return@callSyncMethod )
            }
        })
    }

    override fun onDisable() {
        EntityPickupItemEvent.getHandlerList().unregister(this)
        CraftItemEvent.getHandlerList().unregister(this)
    }

}