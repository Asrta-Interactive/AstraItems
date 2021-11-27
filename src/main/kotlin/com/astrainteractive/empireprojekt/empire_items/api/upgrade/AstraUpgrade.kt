package com.astrainteractive.empireprojekt.empire_items.api.upgrade

import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empireprojekt.empire_items.api.utils.getCustomItemsFiles
import com.astrainteractive.empireprojekt.empire_items.api.utils.getDoubleOrNull
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection

data class AstraUpgrade(
    val id: String,
    val addMax: Double,
    val addMin: Double,
    val attribute: Attribute
) {
    companion object {
        fun getUpgrade(s: ConfigurationSection?): AstraUpgrade? {
            s?:return null
            val id = s.getString("id")?:s.name
            val addMin = s.getDoubleOrNull("addMin")?:return null
            val addMax = s.getDoubleOrNull("addMax")?:return null
            val attributes = (valueOfOrNull<Attribute>(s.getString("attributes")?:""))?:return null
            return AstraUpgrade(id = id,addMin = addMin,addMax = addMax,attribute = attributes)
        }

        fun getUpgrades() =
            getCustomItemsFiles()?.mapNotNull {
                val fileConfig = it.getConfig()
                val section = fileConfig.getConfigurationSection("upgrades")
                section?.getKeys(false)?.mapNotNull {
                    getUpgrade(section.getConfigurationSection(it))
                }
            }?.flatten()

    }
}
