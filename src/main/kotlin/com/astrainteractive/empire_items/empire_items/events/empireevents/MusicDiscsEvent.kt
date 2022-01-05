package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empire_items.empire_items.api.items.data.AstraItem
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Jukebox
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Эвент кастомных музыкальных дисков
 */
class MusicDiscsEvent : IAstraListener {

    /**
     * Список активных jukebox'ов с включенной музыкой
     */
    val activeJukeboxes: MutableMap<Location, AstraItem> = mutableMapOf()


    @EventHandler
    fun blockExplodeEvent(e: BlockExplodeEvent) {
        e.blockList().forEach {
            stopMusic(it.location)
        }
    }

    @EventHandler
    fun playerBlockBreak(e: BlockBreakEvent) {
        stopMusic(e.block.location)
    }

    fun dropDisc(location: Location, item: AstraItem?) {
        location.world?.dropItem(location.add(0.0, 1.0, 0.0), item?.id.toAstraItemOrItem() ?: return)
    }


    /**
     * Срабатывает когда игрок пытается всунуть музкыальный диск.
     */
    @EventHandler
    fun onJukeboxInteract(e: PlayerInteractEvent) {
        val jukebox = isJukebox(e) ?: return

        if (activeJukeboxes.contains(jukebox.location)) {
            stopMusic(jukebox.location)
            e.isCancelled = true

        } else {
            val musicDisc = ItemManager.getItemInfo(e.item?.getAstraID())?: return
            musicDisc.musicDisc?:return
            e.item!!.amount -= 1
            playMusic(musicDisc, jukebox.location)
            e.isCancelled = true
        }
    }


    /**
     * Является ли блок jukebox'ом
     */
    private fun isJukebox(e: PlayerInteractEvent): Jukebox? {
        val block = e.clickedBlock ?: return null
        if (block.type != Material.JUKEBOX || e.action != Action.RIGHT_CLICK_BLOCK)
            return null
        return if (block.state is Jukebox)
            (block.state as Jukebox)
        else null
    }


    /**
     * Включение проигрывания звука
     */
    fun playMusic(item: AstraItem, location: Location) {
        location.world?.playSound(location, "${item.namespace}:${item.musicDisc!!.name}", 2f, 1f)
        activeJukeboxes[location] = item
        getPlayerInDistance(location).forEach { player ->
            val comp = TextComponent(("Играет ${item.displayName}").HEX())
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, comp)

        }
    }

    /**
     * Отключение проигрывания музыки
     */
    fun stopMusic(location: Location) {
        val item = activeJukeboxes[location] ?: return
        for (player in getPlayerInDistance(location))
            player.stopSound("${item.namespace}:${item.musicDisc!!.name}")
        activeJukeboxes.remove(location)
        dropDisc(location, item)
    }


    private fun getPlayerInDistance(location: Location): List<Player> =
        location.world?.players?.filter { it.location.distance((location)) < 16 * 4 } ?: mutableListOf()


    override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        BlockExplodeEvent.getHandlerList().unregister(this)

        for (l in activeJukeboxes.keys)
            stopMusic(l)
    }
}