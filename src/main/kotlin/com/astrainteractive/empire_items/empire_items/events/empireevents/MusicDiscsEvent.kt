package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.models.yml_item.YmlItem
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
class MusicDiscsEvent:EventListener{

    /**
     * Список активных jukebox'ов с включенной музыкой
     */
    val activeJukeboxes: MutableMap<Location, YmlItem> = mutableMapOf()


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

    fun dropDisc(location: Location, item: YmlItem?) {
        location.world?.dropItem(location.add(0.0, 1.0, 0.0), item?.id.toAstraItemOrItem() ?: return)
    }


    /**
     * Срабатывает когда игрок пытается всунуть музкыальный диск.
     */
    val onJukeboxInteract = DSLEvent.event(PlayerInteractEvent::class.java)  { e ->
        val jukebox = isJukebox(e) ?: return@event

        if (activeJukeboxes.contains(jukebox.location)) {
            stopMusic(jukebox.location)
            e.isCancelled = true

        } else {
            val musicDisc = EmpireItemsAPI.itemYamlFilesByID[e.item?.empireID]?: return@event
            musicDisc.musicDisc?:return@event
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
    fun playMusic(item: YmlItem, location: Location) {
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
        for (l in activeJukeboxes.keys)
            stopMusic(l)
    }
}