package com.astrainteractive.empire_items.empire_items.events.resourcepack

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.util.Config
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackEvent : IAstraListener {

    val TAG = "ResourcePack"

    override fun onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerResourcePackStatusEvent.getHandlerList().unregister(this)
    }


    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player
        if (Config.requestPackOnJoin || !p.hasPlayedBefore()) {
            p.performCommand("empack")
            Logger.log("Игрок ${p.name} присоединился впервые. Запрашиваем ресурс-пак",TAG)
        } else
            p.sendTitle(
                EmpirePlugin.translations.resourcePackHintTitle,
                EmpirePlugin.translations.resourcePackHintSubtitle, 5, 20, 5
            )
    }


    @EventHandler
    fun onResourcePack(e: PlayerResourcePackStatusEvent) {
        val p = e.player
        when (e.status) {
            PlayerResourcePackStatusEvent.Status.DECLINED -> {
                Logger.log( "Игрок ${e.player.name} отклонил ресурс-пак",TAG)
                p.sendMessage(EmpirePlugin.translations.resourcePackDeny)
                p.sendMessage(EmpirePlugin.translations.resourcePackDownloadHint)
            }
            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD -> {
                Logger.log("Игроку ${e.player.name} не удалось скачать ресурс-пак",TAG)
                p.kickPlayer(
                    """
                    ${EmpirePlugin.translations.resourcePackDownloadError}
                        """.trimIndent()
                )
                p.sendMessage(EmpirePlugin.translations.resourcePackDownloadError)
            }
            PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED -> {
                if (System.currentTimeMillis() - p.firstPlayed>1000*60*10)
                    return
                p.sendTitle(
                    ":first_join:",
                    "", 5, 1000, 5
                )
                Logger.log("Игроку ${e.player.name} успешно загрузил ресурс-пак",TAG)

            }
            PlayerResourcePackStatusEvent.Status.ACCEPTED -> {
                Logger.log(this.javaClass.name, "Игроку ${e.player.name} принял ресурс-пак")
            }
        }

    }

}