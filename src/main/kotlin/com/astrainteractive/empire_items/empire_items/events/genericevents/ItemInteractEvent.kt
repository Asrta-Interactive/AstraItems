package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.async.AsyncHelper.callSyncMethod
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.models.yml_item.Interact.PlayCommand
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.Future

class ItemInteractEvent {

    private val cooldown = mutableMapOf<String, Long>()

    fun hasCooldown(player: Player, event: String, _cooldown: Int): Boolean {
        val lastUse = cooldown[player.name + event] ?: 0L
        if (System.currentTimeMillis() - lastUse < _cooldown)
            return true
        cooldown[player.name + event] = System.currentTimeMillis()
        return false
    }

    private fun executeEvents(item: ItemStack, player: Player, event: String): Boolean {
        var executed = false
        EmpireItemsAPI.itemYamlFilesByID[item.empireID]?.interact?.forEach { (_, it) ->
            if (!it.eventList.contains(event)) return@forEach
            if (hasCooldown(player, event, it.cooldown ?: 0)) return@forEach
            it.playCommand.values.syncForEach { it.play(player) }
            it.playParticle.values.syncForEach playParticle@{ particle ->
                particle.play(player.location.add(0.0, 1.5, 0.0))
            }
            it.playPotionEffect.values.syncForEach playPotion@{ effect ->
                effect.play(player)
            }
            it.removePotionEffect.syncForEach removeEffect@{ effect ->
                PotionEffectType.getByName(effect)?.let { player.removePotionEffect(it) }
            }
            it.playSound.values.syncForEach { sound ->
                sound.play(player.location)
            }
            executed = true
        }
        return executed
    }

    private inline fun <T> Iterable<T>.syncForEach(crossinline action: (T) -> Unit): Future<Unit>? =
        callSyncMethod { this.forEach(action) }

    val onClick = DSLEvent.event(PlayerInteractEvent::class.java) { e ->
        if (e.hand == EquipmentSlot.HAND)
            executeEvents(item = e.player.inventory.itemInMainHand, player = e.player, event = e.action.name)
        if (e.hand == EquipmentSlot.OFF_HAND)
            executeEvents(item = e.player.inventory.itemInOffHand, player = e.player, event = e.action.name)
    }

    val onDrink = DSLEvent.event(PlayerItemConsumeEvent::class.java) { e ->
        val executed = executeEvents(item = e.player.inventory.itemInMainHand, player = e.player, event = e.eventName)
        if (executed)
            e.replacement = ItemStack(Material.AIR)
    }

    val onFurnaceEnded = DSLEvent.event(FurnaceSmeltEvent::class.java) { e ->
        val id = e.source.empireID ?: return@event
        val returnId = CraftingApi.getFurnaceByInputId(id)?.returns?.toAstraItemOrItem() ?: return@event
        if (e.block.state !is Furnace)
            return@event
        val furnace = e.block.state as Furnace
        furnace.inventory.smelting = returnId
    }

    val onEntityDamage = DSLEvent.event(EntityDamageEvent::class.java) { e ->
        if (e.entity !is Player)
            return@event
        executeEvents(
            item = (e.entity as Player).inventory.itemInMainHand,
            player = (e.entity as Player),
            event = e.eventName
        )
    }

    val onPlayerJoin = DSLEvent.event(PlayerJoinEvent::class.java) { e ->
        cooldown.remove(e.player.name)
    }

    val onPlayerQuit = DSLEvent.event(PlayerQuitEvent::class.java) { e ->
        cooldown.remove(e.player.name)
    }
}