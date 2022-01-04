package com.astrainteractive.empireprojekt.empire_items.events.resourcepack

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.Config
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackEvent : IAstraListener {


    override fun onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerResourcePackStatusEvent.getHandlerList().unregister(this)
    }


    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player
        if (Config.requestPackOnJoin || !p.hasPlayedBefore()) {
            p.performCommand("empack")
            Logger.log(this.javaClass.name, "Игрок ${p.name} присоединился впервые. Запрашиваем ресурс-пак")
        } else
            p.sendTitle(
                EmpirePlugin.translations.RESOURCE_PACK_HINT_TITLE,
                EmpirePlugin.translations.RESOURCE_PACK_HINT_SUBTITLE, 5, 20, 5
            )
    }


    @EventHandler
    fun onResourcePack(e: PlayerResourcePackStatusEvent) {
        val p = e.player
        when (e.status) {
            PlayerResourcePackStatusEvent.Status.DECLINED -> {
                Logger.log(this.javaClass.name, "Игрок ${e.player.name} отклонил ресурс-пак")
                p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DENY)
                p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_SELF)
            }
            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD -> {
                Logger.log(this.javaClass.name, "Игроку ${e.player.name} не удалось скачать ресурс-пак")
                p.kickPlayer(
                    """
                    ${EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR}
                        """.trimIndent()
                )
                p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR)
            }
            PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED -> {
                if (System.currentTimeMillis() - p.firstPlayed>1000*60*10)
                    return
                p.sendTitle(
                    ":first_join:",
                    "", 5, 1000, 5
                )
                Logger.log(this.javaClass.name, "Игроку ${e.player.name} успешно загрузил ресурс-пак")

            }
            PlayerResourcePackStatusEvent.Status.ACCEPTED -> {
                Logger.log(this.javaClass.name, "Игроку ${e.player.name} принял ресурс-пак")
            }
        }

    }

}