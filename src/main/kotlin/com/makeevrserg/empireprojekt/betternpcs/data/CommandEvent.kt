package com.makeevrserg.empireprojekt.betternpcs.data

import org.bukkit.configuration.ConfigurationSection

data class CommandEvent(
    val command: String,
    val asConsole: Boolean
) {
    companion object {
        fun new(section: ConfigurationSection?): List<CommandEvent> {
            val list = mutableListOf<CommandEvent>()
            section ?: return list
            for (keys in section.getKeys(false))
                list.add(
                    CommandEvent(section.getString("command") ?: continue, section.getBoolean("as_console", false))
                )
            return list
        }
    }
}