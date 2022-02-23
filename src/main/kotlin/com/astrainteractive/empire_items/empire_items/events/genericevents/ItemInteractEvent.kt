package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.async.AsyncHelper.callSyncMethod
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.Future

class ItemInteractEvent : EventListener {

    private val cooldown = mutableMapOf<String, Long>()

    fun hasCooldown(player: Player, event: String, _cooldown: Int): Boolean {
        val lastUse = cooldown[player.name + event] ?: 0L
        if (System.currentTimeMillis() - lastUse < _cooldown)
            return true
        cooldown[player.name + event] = System.currentTimeMillis()
        return false
    }

    fun executeEvent(item: ItemStack, player: Player, event: String): Boolean {
        val id = item.getAstraID()
        val itemInfo = ItemApi.getItemInfo(id) ?: return false
        val interact = itemInfo.interact ?: return false
        var executed = false
        interact.forEach {
            executed = true
            if (it.eventList?.contains(event) == false)
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
        return executed
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
        val executed = executeEvent(item = event.player.inventory.itemInMainHand, player = event.player, event = event.eventName)
        if (executed)
            event.replacement = ItemStack(Material.AIR)
    }

    @EventHandler
    fun onFurnaceEnded(event: FurnaceSmeltEvent){
        val id = event.source.getAstraID()?:return
        val returnId = CraftingApi.getFurnaceByInputId(id).firstOrNull { it.returns != null }?.returns?.toAstraItemOrItem() ?:return
        if (event.block.state !is Furnace)
            return
        val furnace = event.block.state as Furnace
        furnace.inventory.smelting = returnId
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
//        AsyncHelper.runBackground {
//            executeEvent(item = event.player.inventory.itemInMainHand, player = event.player, event = event.eventName)
//        }
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