package com.astrainteractive.empire_items.empire_items.events.resourcepack

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.ResourceProvider
import com.astrainteractive.empire_items.api.models.CONFIG
import com.astrainteractive.empire_items.empire_items.util.Translations
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackEvent {
    private val translations: Translations
        get() = ResourceProvider.translations
    val TAG = "ResourcePack"

    val onJoin = DSLEvent.event(PlayerJoinEvent::class.java)  { e ->
        val p = e.player
        if (CONFIG.resourcePack.requestOnJoin || !p.hasPlayedBefore()) {
            Bukkit.getScheduler().runTaskLater(EmpirePlugin.instance, Runnable {
                p.performCommand("empack")
                Logger.log("Игрок ${p.name} присоединился впервые. Запрашиваем ресурс-пак",TAG)
            },300L)

        } else
            p.sendTitle(
                translations.resourcePackHintTitle,
                translations.resourcePackHintSubtitle, 5, 20, 5
            )
    }


    val onResourcePack = DSLEvent.event(PlayerResourcePackStatusEvent::class.java)  { e ->
        val p = e.player
        when (e.status) {
            PlayerResourcePackStatusEvent.Status.DECLINED -> {
                Logger.log( "Игрок ${e.player.name} отклонил ресурс-пак",TAG)
                p.sendMessage(translations.resourcePackDeny)
                p.sendMessage(translations.resourcePackDownloadHint)
            }
            PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD -> {
                Logger.log("Игроку ${e.player.name} не удалось скачать ресурс-пак",TAG)
                p.sendMessage(translations.resourcePackDownloadError)
            }
            PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED -> {
                if (System.currentTimeMillis() - p.firstPlayed>1000*60*10)
                    return@event
                p.sendTitle(
                    ":first_join:",
                    "", 5, 30, 5
                )
                Logger.log("Игроку ${e.player.name} успешно загрузил ресурс-пак",TAG)

            }
            PlayerResourcePackStatusEvent.Status.ACCEPTED -> {
                Logger.log(this.javaClass.name, "Игроку ${e.player.name} принял ресурс-пак")
            }
        }

    }

}