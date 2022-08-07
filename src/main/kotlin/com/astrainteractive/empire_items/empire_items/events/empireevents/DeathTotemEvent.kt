package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class DeathTotemEvent{

    private fun ItemStack?.isDeathTotem() =
        this?.itemMeta?.hasPersistentData(BukkitConstants.TOTEM_OF_DEATH) == true

    private fun isHoldTotem(inv:PlayerInventory): Boolean =
         inv.itemInMainHand.isDeathTotem() || inv.itemInOffHand.isDeathTotem()

    val playerInteractEvent = DSLEvent.event(PlayerInteractEvent::class.java)  { e ->
        if (isHoldTotem(e.player.inventory))
            killPlayer(e.player)
    }
    val playerItemHeldEvent = DSLEvent.event(PlayerItemHeldEvent::class.java)  { e ->
        if (isHoldTotem(e.player.inventory))
            killPlayer(e.player)
    }

    private fun killPlayer(player:Player){
        player.damage(999999999999.0)
        Bukkit.getScheduler().runTaskLater(AstraLibs.instance, Runnable {
            player.damage(999999999999.0)

        },20L*2)
        player.sendMessage("#cf2d04${ChatColor.MAGIC}Голос ${ChatColor.AQUA}-> #cf2d04Ты недостоин".HEX())
    }
}