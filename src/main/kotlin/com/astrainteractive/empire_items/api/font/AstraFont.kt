package com.astrainteractive.empire_items.api.font

import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection


data class AstraFont(
    val id: String,
    val path: String,
    val height: Int,
    val ascent: Int,
    val char: String,
    val blockSend: Boolean,
    val namespace: String
) {
    companion object {
        private val count: Int
            get() = 0x3400

        fun getFonts() = getCustomItemsFiles()?.mapNotNull fileManager@{fileManager->
            val fileConfig = fileManager.getConfig()
            val mainSection = fileConfig.getConfigurationSection("fontImages")
            mainSection?.getKeys(false)?.mapNotNull {id->
                val s: ConfigurationSection = mainSection.getConfigurationSection(id) ?: return@mapNotNull null
                AstraFont(
                    id = s.getString("id") ?: s.name,
                    path = s.getString("path") ?: return@mapNotNull null,
                    height = s.getInt("height", 10),
                    ascent = s.getInt("ascent", 13),
                    char = (count + s.getInt("data")).toChar().toString(),
                    blockSend = s.getBoolean("blockSend", false),
                    namespace = fileConfig.getString("namespace", "minecraft")!!
                )
            }
        }?.flatten() ?: listOf()
    }
}


