package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData

import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

class SoulBindEvent: EventListener {


    val playerDiedMap= mutableMapOf<String,MutableList<ItemStack>>()
    val playerDiedLocationMap = mutableMapOf<String,Location>()

    @EventHandler
    fun playerDieEvent(e:PlayerDeathEvent){
        val player = e.entity
        playerDiedMap[player.uniqueId.toString()] = mutableListOf()
        for (item in e.drops.toList()){
            if (item.itemMeta?.hasPersistentData(BukkitConstants.SOUL_BIND)==true){
                playerDiedMap[player.uniqueId.toString()]!!.add(item)
                e.drops.remove(item)
            }
        }
        if (playerDiedMap[player.uniqueId.toString()].isNullOrEmpty())
            playerDiedMap.remove(player.uniqueId.toString())
        else
            playerDiedLocationMap[player.uniqueId.toString()] = player.location
    }

    @EventHandler
    fun playerRespawnEvent(e:PlayerRespawnEvent){
        val player = e.player
        for (item in playerDiedMap[player.uniqueId.toString()]?: mutableListOf()){
            player.inventory.addItem(item)
        }
        playerDiedMap.remove(e.player.uniqueId.toString())
        playerDiedLocationMap.remove(e.player.uniqueId.toString())
    }
    @EventHandler
    fun playerDisconnectEvent(e:PlayerQuitEvent){
        val location = playerDiedLocationMap[e.player.uniqueId.toString()]?:return
        for (item in playerDiedMap[e.player.uniqueId.toString()]?: mutableListOf())
            location.world?.dropItem(location,item)?:continue
        playerDiedMap.remove(e.player.uniqueId.toString())
        playerDiedLocationMap.remove(e.player.uniqueId.toString())
    }

    override fun onDisable() {
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
        PlayerRespawnEvent.getHandlerList().unregister(this)
    }

}