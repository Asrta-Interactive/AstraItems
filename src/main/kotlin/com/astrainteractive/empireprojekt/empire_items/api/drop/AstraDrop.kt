package com.astrainteractive.empireprojekt.empire_items.api.drop

import com.astrainteractive.empireprojekt.empire_items.api.utils.getCustomItemsFiles
import com.astrainteractive.empireprojekt.empire_items.util.YamlParser
import org.bukkit.configuration.ConfigurationSection

data class AstraDrop(
    val dropFrom: String,
    val id: String,
    val minAmount: Int,
    val maxAmount: Int,
    val chance: Double
) {

    companion object {
        fun getDrops(): List<AstraDrop> {
            val drops = getCustomItemsFiles()?.flatMap { fileManager ->
                getMapDrop(fileManager.getConfig().getConfigurationSection("loot")?:return@flatMap emptyList())
            }?: listOf()
            return drops
        }

        private fun getMapDrop(s: ConfigurationSection?) = s?.getKeys(false)?.flatMap { dropFrom ->
            val section = s.getConfigurationSection(dropFrom)
            //loot.<dropFrom>.<itemId>
            val drops = section?.getKeys(false)?.mapNotNull { itemId ->
                getSingleDrop(dropFrom, itemId, section.getConfigurationSection(itemId))
            } ?: listOf()
            drops
        } ?: listOf()

        private fun getSingleDrop(_dropFrom: String, _itemId: String, s: ConfigurationSection?): AstraDrop? {

            val parser = YamlParser()
            val res = parser.configurationSectionToClass<AstraDrop>(s?:return null)?:return null
            val id = parser.fixNull(res.id,_itemId)
            val dropFrom = parser.fixNull(res.dropFrom,_dropFrom)
            return AstraDrop(
                dropFrom = dropFrom,
                id = id,
                minAmount = res.minAmount,
                maxAmount = res.maxAmount,
                chance = res.chance
            )
        }
    }
}
