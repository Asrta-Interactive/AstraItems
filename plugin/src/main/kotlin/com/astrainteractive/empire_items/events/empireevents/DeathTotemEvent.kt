package com.astrainteractive.empire_items.events.empireevents

import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.persistence.Persistence.hasPersistentData

class DeathTotemEvent{

    private fun ItemStack?.isDeathTotem() =
        this?.itemMeta?.hasPersistentData(BukkitConstants.TOTEM_OF_DEATH) == true

    private fun isHoldTotem(inv:PlayerInventory): Boolean =
         inv.itemInMainHand.isDeathTotem() || inv.itemInOffHand.isDeathTotem()

    val playerInteractEvent = DSLEvent.event<PlayerInteractEvent>  { e ->
        playEvent(e.player)
    }
    val playerItemHeldEvent = DSLEvent.event<PlayerItemHeldEvent>  { e ->
        playEvent(e.player)
    }

    private fun playEvent(player:Player){
        if (!isHoldTotem(player.inventory)) return
        player.damage(99999.0)
        player.sendMessage("#cf2d04${ChatColor.MAGIC}Голос ${ChatColor.AQUA}-> #cf2d04Ты недостоин".HEX())

    }
}