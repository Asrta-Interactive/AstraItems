package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getItemInfo
import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.api.utils.getPersistentData
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.*

import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GrapplingHook : IAstraListener {
    private val activeHooks = mutableMapOf<String, Location>()
    private fun unCastHook(itemStack: ItemStack, player: Player) {
        val state = itemStack.itemMeta.getPersistentData(BukkitConstants.GRAPPLING_HOOK) ?: return
        val defaultcmd = state.split(";").firstOrNull()?.getItemInfo()?.customModelData ?: return
        val castedcmd = state.split(";").lastOrNull()?.getItemInfo()?.customModelData ?: return

        if (activeHooks.contains(player.name)) {
            player.playSound(Sound.BLOCK_CHAIN_BREAK)
            activeHooks.remove(player.name)
            itemStack.setCustomModelDate(defaultcmd)
        } else {
            itemStack.setCustomModelDate(defaultcmd)
            player.playSound(Sound.ITEM_ARMOR_EQUIP_CHAIN)
        }
    }

    private fun castHook(itemStack: ItemStack, player: Player, location: Location?) {
        val state = itemStack.itemMeta.getPersistentData(BukkitConstants.GRAPPLING_HOOK) ?: return
        val defaultcmd = state.split(";").firstOrNull()?.getItemInfo()?.customModelData ?: return
        val castedcmd = state.split(";").lastOrNull()?.getItemInfo()?.customModelData ?: return

        itemStack.setCustomModelDate(castedcmd)
        player.playSound(Sound.BLOCK_CHAIN_PLACE)
        if (location==null) {
            unCastHook(itemStack, player)
            return
        }
        activeHooks[player.name] = location
    }

    fun Player.playSound(s: Sound) {
        location.world.playSound(location, s, 1f, 1f)
    }

    fun ItemStack.setCustomModelDate(data: Int) {
        val meta = itemMeta
        meta.setCustomModelData(data)
        this.itemMeta = meta
    }

    private fun traceEffect(l: Location): ParticleBuilder = ParticleBuilder(Particle.REDSTONE)
        .count(20)
        .force(true)
        .extra(0.06)
        .data(null)
        .color(Color.BLACK)
        .location(l.world, l.x, l.y, l.z)

    private fun bumpEffect(l: Location): ParticleBuilder = ParticleBuilder(Particle.SMOKE_LARGE)
        .count(70)
        .force(true)
        .extra(0.06)
        .data(null)
        .location(l.world, l.x, l.y, l.z)

    @EventHandler
    fun playerHookShootEvent(e: PlayerInteractEvent) {
        val item = e.player.inventory.itemInMainHand
        item.getAstraID() ?: return
        val state = item.itemMeta.getPersistentData(BukkitConstants.GRAPPLING_HOOK) ?: return
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            unCastHook(item, e.player)
            return
        }
        if (e.player.gameMode != GameMode.CREATIVE && (e.player as HumanEntity).hasCooldown(item.type))
            return

        val player = e.player
        if (activeHooks.containsKey(player.name)) {
            val location = activeHooks[player.name]!!.clone()
            location.pitch = e.player.location.pitch
            location.yaw = e.player.location.yaw
            unCastHook(item, e.player)
            val distance = location.clone().distance(player.location)
            if (distance > 400)
                return

            val v3 = location.clone().subtract(player.location)
            val multiply = 0.4 - (distance / 1000)
            player.velocity = v3.toVector().multiply(multiply / 2)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 45, 1, false, false, false))
            (e.player as HumanEntity).setCooldown(item.type, 50)
            return
        }


        var l = player.location.clone().add(0.0, 1.5, 0.0)
        for (i in 0 until 200) {
            traceEffect(l).spawn()
            l = l.add(l.direction.x, l.direction.y - i / (350 * 0.9), l.direction.z)
            if (!l.block.isPassable) {
                activeHooks[player.name] = l.add(0.0, 1.0, 0.0)
                bumpEffect(l).spawn()
                castHook(item, player, l)
                return
            }
        }
        castHook(item,player,null)
    }

    @EventHandler
    fun itemHeldEvent(e: PlayerItemHeldEvent) {
        activeHooks[e.player.name] ?: return
        val hook = e.player.inventory.getItem(e.previousSlot) ?: return
        hook.itemMeta.getPersistentData(BukkitConstants.GRAPPLING_HOOK) ?: return
        unCastHook(hook, e.player)
    }

    @EventHandler
    fun playerLeaveEvent(e: PlayerQuitEvent) {
        activeHooks.remove(e.player.name)
    }

    override fun onDisable() {
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }
}