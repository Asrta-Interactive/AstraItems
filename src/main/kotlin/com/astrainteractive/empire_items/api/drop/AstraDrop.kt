package com.astrainteractive.empire_items.api.drop

import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection
import kotlin.random.Random


data class AstraDrop(
    val dropFrom: String,
    val id: String,
    val minAmount: Int = 0,
    val maxAmount: Int=minAmount+1,
    val chance: Double=0.0
) {
    val calculatedAmount: Int
        get() = Random(System.currentTimeMillis()).nextInt(minAmount, maxAmount + 1)

    companion object {
        fun getDrops(): List<AstraDrop> = getCustomItemsFiles()?.flatMap { fileManager ->
            getMapDrop(
                fileManager.getConfig().getConfigurationSection("loot") ?: return@flatMap emptyList<AstraDrop>()
            )
        } ?: listOf()


        private fun getMapDrop(section: ConfigurationSection?) = section?.getKeys(false)?.mapNotNull { itemId ->
            val s = section.getConfigurationSection(itemId) ?: return@mapNotNull null
            AstraDrop(
                dropFrom = s.getString("dropFrom") ?: return@mapNotNull null,
                id = s.getString("id") ?: itemId,
                minAmount = s.getInt("minAmount", 0),
                maxAmount = s.getInt("maxAmount", s.getInt("minAmount", 0)),
                chance = s.getDouble("chance", 0.0)
            )
        } ?: listOf()


    }


}
