package com.makeevrserg.empireprojekt.ESSENTIALS.sit

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.translations
import com.makeevrserg.empireprojekt.util.Translations
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.entity.EntityDismountEvent

class SitEvent : Listener {


    private val sitPlayers = mutableMapOf<Player, ArmorStand>()
    fun sitPlayer(player: Player, loc: Location? = null) {
        val location = loc ?: player.location
        if (sitPlayers.contains(player)) {
            player.sendMessage(translations.SIT_ALREADY)
            return
        }
        if (player.isFlying) {
            player.sendMessage(translations.SIT_IN_AIR)
            return
        }
        if (player.location.block.getRelative(BlockFace.DOWN).type == Material.AIR) {
            player.sendMessage(translations.SIT_IN_AIR)
            return
        }


        val chair = location.world?.spawnEntity(location.add(0.5, -1.6, 0.5), EntityType.ARMOR_STAND) as ArmorStand
        chair.setGravity(false)
        chair.isVisible = false
        chair.isInvulnerable = false
        chair.addPassenger(player)
        sitPlayers[player] = chair


    }

    private fun stopSitPlayer(player: Player) {
        val armorStand = sitPlayers[player] ?: return
        armorStand.remove()
        sitPlayers.remove(player)
    }
    @EventHandler
    fun onDeathEvent(e:PlayerDeathEvent){
        stopSitPlayer(e.entity)
    }

    @EventHandler
    fun onTeleportEvent(e:PlayerTeleportEvent){
        stopSitPlayer(e.player)
    }


    @EventHandler
    fun playerInteractEvent(e: PlayerInteractEvent) {
        if (e.action!=Action.RIGHT_CLICK_BLOCK)
            return
//        val player = e.player
//        val block = e.clickedBlock ?: return
//        val blockName = block.type.name.toLowerCase()
//
//        when {
//            blockName.contains("slab") -> sitPlayer(player, block.location.add(0.0,0.4,0.0))
//            blockName.contains("stairs") -> sitPlayer(player, block.location.add(0.0,0.5,0.0))
//            blockName.contains("carpet") -> sitPlayer(player, block.location)
//        }
    }

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) {
        stopSitPlayer(e.player)
    }


    @EventHandler
    fun onDismount(e: EntityDismountEvent) {
        if (e.entity !is Player)
            return
        stopSitPlayer(e.entity as Player)
    }

    companion object {
        lateinit var instance: SitEvent
            private set
    }

    init {
        instance = this
        EmpirePlugin.instance.server.pluginManager.registerEvents(this,
            EmpirePlugin.instance
        )
    }


    fun onDisable() {
        for (player in sitPlayers.keys)
            sitPlayers[player]!!.remove()
        sitPlayers.clear()
        EntityDismountEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
        PlayerTeleportEvent.getHandlerList().unregister(this)
    }

}