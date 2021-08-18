package makeevrserg.empireprojekt.events.resourcepack

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.comphenix.protocol.wrappers.WrappedChatComponent
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
        if (EmpirePlugin.empireConfig.downloadPackOnJoin || !p.hasPlayedBefore()) {
            p.performCommand("empack")
            println("Игрок ${p.name} присоединился впервые. Запрашиваем ресурс-пакS")
        }
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
            println("Игрок ${e.player.name} отклонил ресурс-пак")
            p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DENY)
            p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_SELF)
        } else if (e.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            println("Игроку ${e.player.name} не удалось скачать ресурс-пак")
            p.kickPlayer(
                """
                ${EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR}
                    """.trimIndent()
            )
            p.sendMessage(EmpirePlugin.translations.RESOURCE_PACK_DOWNLOAD_ERROR)
        }
    }

}