package com.astrainteractive.empire_items.empire_items.events.empireevents

import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData

import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

class SoulBindEvent{


    val playerDiedMap= mutableMapOf<String,MutableList<ItemStack>>()
    val playerDiedLocationMap = mutableMapOf<String,Location>()

    val playerDieEvent = DSLEvent.event(PlayerDeathEvent::class.java)  { e ->
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

    val playerRespawnEvent = DSLEvent.event(PlayerRespawnEvent::class.java)  { e ->
        val player = e.player
        for (item in playerDiedMap[player.uniqueId.toString()]?: mutableListOf()){
            player.inventory.addItem(item)
        }
        playerDiedMap.remove(e.player.uniqueId.toString())
        playerDiedLocationMap.remove(e.player.uniqueId.toString())
    }
    val playerDisconnectEvent = DSLEvent.event(PlayerQuitEvent::class.java)  { e ->
        val location = playerDiedLocationMap[e.player.uniqueId.toString()]?:return@event
        for (item in playerDiedMap[e.player.uniqueId.toString()]?: mutableListOf())
            location.world?.dropItem(location,item)?:continue
        playerDiedMap.remove(e.player.uniqueId.toString())
        playerDiedLocationMap.remove(e.player.uniqueId.toString())
    }
}