package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackEvent : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerResourcePackStatusEvent.getHandlerList().unregister(this)
    }


    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player
        if (plugin.config.downloadPackOnJoin)
            p.performCommand("empack")
        else
            p.sendTitle(
                plugin.translations.RESOURCE_PACK_HINT_TITLE,
                plugin.translations.RESOURCE_PACK_HINT_SUBTITLE, 5, 200, 5
            )
    }


    @EventHandler
    fun onResourcePack(e: PlayerResourcePackStatusEvent) {
        val p = e.player
        if (e.status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            p.sendMessage(plugin.translations.RESOURCE_PACK_DENY)
            p.sendMessage(plugin.translations.RESOURCE_PACK_DOWNLOAD_SELF)
        } else if (e.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            p.kickPlayer(
                """
                ${plugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR}
                    """.trimIndent()
            )
            p.sendMessage(plugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR)
        }
    }

}