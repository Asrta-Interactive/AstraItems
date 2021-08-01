package com.makeevrserg.empireprojekt.events.upgrades

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.ConfigurationSection

class UpgradesManager {

    data class ItemUpgrade(
        val attr: String,
        val add_min: Double,
        val add_max: Double
    )

    public val _upgradesMap: MutableMap<String, List<ItemUpgrade>> = mutableMapOf()
    private fun initList() {
        val section: ConfigurationSection = EmpirePlugin.empireFiles.upgradesFile.getConfig() ?: return
        for (itemID in section.getKeys(false)) {
            val upgradesList: MutableList<ItemUpgrade> = mutableListOf()
            for (attribute in section.getConfigurationSection(itemID)!!.getKeys(false)) {
                val attrSect: ConfigurationSection =
                    section.getConfigurationSection(itemID)!!.getConfigurationSection(attribute)!!
                upgradesList.add(
                    ItemUpgrade(
                        attribute,
                        attrSect.getDouble("add_min", 0.0),
                        attrSect.getDouble("add_max", 0.0)
                    )
                )
            }
            _upgradesMap[itemID] = upgradesList
        }
    }

    init {
        initList()
    }

}