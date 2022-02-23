package com.astrainteractive.empire_items.api.upgrade

import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import com.astrainteractive.empire_items.api.utils.getDoubleOrNull
import org.bukkit.attribute.Attribute


data class AstraUpgrade(

    val id: String,
    val addMax: Double,
    val addMin: Double,
    val attribute: Attribute
) {
    companion object {
        fun getUpgrades() =
            getCustomItemsFiles()?.mapNotNull file@{
                val fileConfig = it.getConfig()
                val section = fileConfig.getConfigurationSection("upgrades")
                section?.getKeys(false)?.mapNotNull {
                    val s = section.getConfigurationSection(it) ?: return@mapNotNull null
                    AstraUpgrade(
                        id = s.getString("id") ?: s.name,
                        addMin = s.getDoubleOrNull("addMin") ?: return null,
                        addMax = s.getDoubleOrNull("addMax") ?: return null,
                        attribute = (valueOfOrNull<Attribute>(s.getString("attributes") ?: "")) ?: return null
                    )
                }
            }?.flatten()

    }
}
