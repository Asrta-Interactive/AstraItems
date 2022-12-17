package com.astrainteractive.empire_items.events.api_events

import com.astrainteractive.empire_items.di.empireUtilsModule
import com.astrainteractive.empire_itemss.api.utils.EmpireUtils
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import ru.astrainteractive.astralibs.di.getValue

object PlibFontListener : ProtocolLibListener(
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.SCOREBOARD_OBJECTIVE,
    PacketType.Play.Server.SCOREBOARD_TEAM,
    PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE,
    PacketType.Play.Server.SCOREBOARD_SCORE,
    PacketType.Play.Server.PLAYER_INFO,
    PacketType.Play.Server.SET_TITLE_TEXT,
    PacketType.Play.Server.SET_SUBTITLE_TEXT,
    PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER,
    PacketType.Play.Server.OPEN_WINDOW,
    PacketType.Play.Server.CHAT,
    PacketType.Play.Client.CHAT,

    ) {
    val empireUtils by empireUtilsModule
    override fun onPacketReceiving(event: PacketEvent) {
        val packet = event.packet

        for (i in 0 until packet.chatComponents.size()) {
            chatCompToEmoji(packet, i)
            for (j in 0 until packet.modifier.size()) {
                val obj = packet.modifier.read(j) ?: continue
                if (obj is TranslatableComponent)
                    packet.modifier.write(j, obj.args(getListComponent(obj)))

                if (obj is TextComponent)
                    packet.modifier.write(j, convertTextComponent(obj))
            }

        }
    }

    private fun chatCompToEmoji(packet: PacketContainer, i: Int) {
        val chatComponent = packet.chatComponents.read(i) ?: return
        chatComponent.json = empireUtils.emojiPattern(chatComponent.json)
        packet.chatComponents.write(i, chatComponent)
    }

    private fun convertTextComponent(textComponent: TextComponent): Component {
        val line = empireUtils.emojiPattern(GsonComponentSerializer.gson().serialize(textComponent))
        return GsonComponentSerializer.gson().deserialize(line)
    }

    private fun getListComponent(translatableComponent: TranslatableComponent): MutableList<Component> {
        val listComponent = mutableListOf<Component>()
        for (arg in translatableComponent.args()) {
            val compo = convertTextComponent(arg as TextComponent)
            listComponent.add(compo)
        }
        return listComponent
    }

    override fun onPacketSending(event: PacketEvent) {
        val packet = event.packet


        for (i in 0 until packet.chatComponents.size()) {
            chatCompToEmoji(packet, i)
            for (j in 0 until packet.modifier.size()) {
                val obj = packet.modifier.read(j) ?: continue
                if (obj is TranslatableComponent)
                    packet.modifier.write(j, obj.args(getListComponent(obj)))

                if (obj is TextComponent)
                    packet.modifier.write(j, convertTextComponent(obj))
            }

        }
    }
}
