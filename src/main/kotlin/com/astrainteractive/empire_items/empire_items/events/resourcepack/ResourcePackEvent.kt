package com.astrainteractive.empire_items.empire_items.events.resourcepack

import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.util.Config
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackEvent {

    val TAG = "ResourcePack"

    val onJoin = DSLEvent.event(PlayerJoinEvent::class.java)  { e ->
        val p = e.player
        if (Config.requestPackOnJoin || !p.hasPlayedBefore()) {
            Bukkit.getScheduler().runTaskLater(EmpirePlugin.instance, Runnable {
                p.performCommand("empack")
                Logger.log("Игрок ${p.name} присоединился впервые. Запрашиваем ресурс-пак",TAG)
            },300L)

        } else
            p.sendTitle(
                EmpirePlugin.translations.resourcePackHintTitle,
                EmpirePlugin.translations.resourcePackHintSubtitle, 5, 20, 5
            )
    }


    val onResourcePack = DSLEvent.event(PlayerResourcePackStatusEvent::class.java)  { e ->
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
                    return@event
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