package com.makeevrserg.empireprojekt.essentials.music_disc

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.asEmpireItem
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.getEmpireID
import com.makeevrserg.empireprojekt.empirelibs.HEX
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.items.data.EmpireItem
import net.md_5.bungee.api.ChatColor
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
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Эвент кастомных музыкальных дисков
 */
class MusicDiscsNewEvent : IEmpireListener {

    /**
     * Список активных jukebox'ов с включенной музыкой
     */
    val activeJukeboxes: MutableMap<Location, EmpireItem> = mutableMapOf()


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

    fun dropDisc(location: Location, item: EmpireItem?) {
        location.world?.dropItem(location.add(0.0, 1.0, 0.0), item?.id.asEmpireItem() ?: return)
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
            val musicDisc = ItemsAPI.getEmpireItemInfo(e.item.getEmpireID()?:return)?: return
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
    fun playMusic(item: EmpireItem, location: Location) {
        location.world?.playSound(location, "${item.namespace}:${item.musicDisc!!.song}", 2f, 1f)
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
            player.stopSound("${item.namespace}:${item.musicDisc!!.song}")
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