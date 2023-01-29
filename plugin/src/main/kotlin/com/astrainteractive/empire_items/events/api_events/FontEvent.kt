package com.astrainteractive.empire_items.events.api_events


import com.astrainteractive.empire_items.di.empireUtilsModule
import com.astrainteractive.empire_items.util.MoreReflectedUtil
import io.netty.channel.Channel
import net.minecraft.network.protocol.game.PacketListenerPlayIn
import net.minecraft.network.protocol.game.PacketPlayInChat
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.AstraPacketReader

object PacketPlayInChatListener : AstraPacketReader<PacketListenerPlayIn, PacketPlayInChat>(PacketPlayInChat::class.java) {
    val empireUtils by empireUtilsModule

    override val Player.provideChannel: Channel
        get() = (this as CraftPlayer).handle.b.b.m

    override fun readPacket(player: Player, packet: PacketPlayInChat) {
        val value = packet.b()
        val converted = empireUtils.emojiPattern(value)
        kotlin.runCatching {
            MoreReflectedUtil.setFinalField(packet, converted, "b", true)
        }.onFailure { it.printStackTrace() }
    }
}