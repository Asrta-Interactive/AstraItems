package com.makeevrserg.empireprojekt.ESSENTIALS.PlayerSkin

import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.mojang.authlib.properties.Property
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import scala.sys.Prop
import kotlin.random.Random

class SkinGrabber {

    companion object {
        public fun changeSkin(player: Player, skinName: String) {
            val handle = (player as CraftPlayer).handle
            val profile = handle.profile
            val connection = handle.b
            connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, handle))

            profile.properties.removeAll("textures")
            profile.properties.put("textures", getSkin(skinName))
            connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, handle))
        }

        public fun getSkin(skinName: String): Property? {
            val skin = EmpireUtils.getSkinByPlayerName(name = skinName) ?: return null
            return Property("textures", skin[0], skin[1])
        }
    }
}