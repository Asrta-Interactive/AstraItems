package com.astrainteractive.empire_items.modules.hud

import com.astrainteractive.astralibs.uuid
import com.astrainteractive.empire_items.models.FontImage
import com.astrainteractive.empire_items.modules.hud.thirst.RepeatableTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object HudView {
    private val playerHud: MutableMap<String, MutableSet<PlayerHud>> = mutableMapOf()

    fun send(player: Player, font: FontImage, id: String, position: Int) = synchronized(HudView) {
        val playerHuds = playerHud[player.uuid] ?: return
        playerHuds.removeAll { it.id == id }
        playerHuds.add(PlayerHud(id, position, font))
        playerHud[player.uuid] = playerHuds
    }

    fun onEnable() {
        hudNotifier()
    }

    private fun hudNotifier() = RepeatableTask(1000L) {
        playerHud.forEach { (k, v) ->
            val p = Bukkit.getPlayer(UUID.fromString(k)) ?: return@forEach
            var line = ""
            v.sortedBy { it.xPosition }.forEach {
                line = HudManager.addFontElement(line, it.astraFont, it.xPosition)
            }
            p.sendActionBar(line)
        }
    }

    fun onDisable() {
        playerHud.clear()
    }

    fun onPlayerJoin(player: Player) = synchronized(HudView) {
        playerHud[player.uuid] = mutableSetOf()
    }

    fun onPlayerQuit(player: Player) = synchronized(HudView) {
        playerHud.remove(player.uuid)
    }
}