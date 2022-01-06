package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import com.destroystokyo.paper.ParticleBuilder
import kotlinx.coroutines.launch
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.Future

class GenericEvents : IAstraListener {

    private val cooldown = mutableMapOf<String, Long>()

    fun hasCooldown(player: Player, event: String, _cooldown: Int): Boolean {
        val lastUse = cooldown[player.name + event] ?: 0L
        if (System.currentTimeMillis() - lastUse < _cooldown)
            return true
        cooldown[player.name + event] = System.currentTimeMillis()
        return false
    }

    fun executeEvent(item: ItemStack, player: Player, event: String) {
        val id = item.getAstraID()
        val itemInfo = ItemManager.getItemInfo(id) ?: return
        val interact = itemInfo.interact ?: return
        interact.forEach {
            if (it.eventList?.contains(event.uppercase()) == false)
                return@forEach
            if (hasCooldown(player, event, it.cooldown ?: 0))
                return@forEach

            it.playCommand?.syncForEach { cmd ->
                    if (cmd.asConsole)
                        AstraLibs.instance.server.dispatchCommand(AstraLibs.instance.server.consoleSender, cmd.command)
                    else player.performCommand(cmd.command)
            }
            it.playParticle?.syncForEach playParticle@{ particle ->
                    ParticleBuilder(valueOfOrNull<Particle>(particle.name) ?: return@playParticle)
                        .count(particle.count)
                        .extra(particle.time)
                        .location(player.location.add(0.0, 1.5, 0.0)).spawn()
            }
            it.playPotionEffect?.syncForEach playPotion@{ effect ->
                    player.addPotionEffect(
                        PotionEffect(
                            PotionEffectType.getByName(effect.effect) ?: return@playPotion,
                            effect.duration,
                            effect.amplifier
                        )
                    )

            }
            it.potionEffectsRemove?.syncForEach removeEffect@{ effect ->
                    player.removePotionEffect(PotionEffectType.getByName(effect) ?: return@removeEffect)
            }
            it.playSound?.syncForEach { sound ->
                    player.world.playSound(
                        player.location,
                        sound.name,
                        sound.volume ?: 1.0f,
                        sound.pitch ?: 1.0f
                    )
            }
        }
    }

    private inline fun <T> Iterable<T>.syncForEach(crossinline action: (T) -> Unit): Future<Unit>? =
        callSyncMethod{
            for (element in this) action(element)
        }


    @EventHandler
    fun onClick(event: PlayerInteractEvent) {

        if (event.hand == EquipmentSlot.HAND)
            executeEvent(item = event.player.inventory.itemInMainHand, player = event.player, event = event.action.name)
        if (event.hand == EquipmentSlot.OFF_HAND)
            executeEvent(item = event.player.inventory.itemInOffHand, player = event.player, event = event.action.name)
    }

    @EventHandler
    fun onDrink(event: PlayerItemConsumeEvent) {
        executeEvent(item = event.player.inventory.itemInMainHand, player = event.player, event = event.eventName)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player)
            return
        executeEvent(
            item = (event.entity as Player).inventory.itemInMainHand,
            player = (event.entity as Player),
            event = event.eventName
        )
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        AsyncHelper.runBackground {
            executeEvent(item = event.player.inventory.itemInMainHand, player = event.player, event = event.eventName)
        }
    }


    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        cooldown.remove(e.player.name)
    }


    @EventHandler
    fun onPlayerJoin(e: PlayerQuitEvent) {
        cooldown.remove(e.player.name)
    }

    override fun onDisable() {
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
        PlayerItemConsumeEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)

    }
}