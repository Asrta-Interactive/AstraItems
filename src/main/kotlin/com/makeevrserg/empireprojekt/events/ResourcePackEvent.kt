package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.IEmpireListener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackEvent : IEmpireListener {



    override fun onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerResourcePackStatusEvent.getHandlerList().unregister(this)
    }


    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player
        if (EmpirePlugin.empireConfig.downloadPackOnJoin)
            p.performCommand("empack")
        else
            p.sendTitle(
                EmpirePlugin.translations.RESOURCE_PACK_HINT_TITLE,
                EmpirePlugin.translations.RESOURCE_PACK_HINT_SUBTITLE, 5, 200, 5
            )
    }


    @EventHandler
    fun onResourcePack(e: PlayerResourcePackStatusEvent) {
        val p = e.player
        if (e.status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DENY)
            p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_SELF)
        } else if (e.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            p.kickPlayer(
                """
                ${EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR}
                    """.trimIndent()
            )
            p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR)
        }
    }

}