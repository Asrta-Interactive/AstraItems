package com.astrainteractive.empire_items.api.items.data.interact

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.catching
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

data class PlayCommand(
    val command: String,
    val asConsole: Boolean
) {
    fun play(player: Player?) {
        val parsed = catching { PlaceholderAPI.setPlaceholders(player, command); } ?: command
        if (asConsole)
            AstraLibs.instance.server.dispatchCommand(AstraLibs.instance.server.consoleSender, parsed)
        else player?.performCommand(parsed)
    }

    companion object {
        fun getMultiPlayCommand(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getSinglePlayCommand(s.getConfigurationSection(it))
            }

        private fun getSinglePlayCommand(s: ConfigurationSection?): PlayCommand? {
            return PlayCommand(s?.getString("command") ?: return null, s.getBoolean("asConsole", false))
        }

    }
}
