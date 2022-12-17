package com.astrainteractive.empire_items.events.genericevents

import com.astrainteractive.empire_items.di.craftingApiModule
import ru.astrainteractive.astralibs.*
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.empireID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue

/**
 * Показывать игроку рецепт кастомных предметов
 */
class PlayerShowRecipeKeyEvent {
    private val craftingApi by craftingApiModule
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
        PluginScope.launch {
            val mainItemId = itemStack.empireID ?: itemStack.type.name
            val ids = mutableListOf(mainItemId).apply { addAll(craftingApi.usedInCraft(mainItemId)) }
            val toDiscover =
                ids.flatMap { craftingApi.getKeysById(it) ?: listOf() }.filter { !player.hasDiscoveredRecipe(it) }
            PluginScope.launch(Dispatchers.BukkitMain) {
                toDiscover.forEach {
                    Logger.log("Player ${player.name} discovered recipe ${it}", "Crafting", consolePrint = false)
                    player.discoverRecipe(it)
                }
            }
        }
    }
}