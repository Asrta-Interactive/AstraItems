package com.astrainteractive.empire_items.modules.enchants.data

import com.astrainteractive.astralibs.FileManager
import org.bukkit.configuration.ConfigurationSection

data class EmpireEnchantement(
    val id: String,
    val maxLevel: Int = 5,
    val expPerLevel:Int = 1,
    val chances: IntArray,
    val value: Double,
    val increaseModifier: Double,
    val enchantingTableEnabled:Boolean = true,
    val anvilEnchantingEnabled:Boolean = true
) {
    companion object {
        fun loadALl(): List<EmpireEnchantement> {
            val fConfig = FileManager("modules/empire_enchants.yml").getConfig().getConfigurationSection("enchants")
            return fConfig?.getKeys(false)?.mapNotNull { id ->
                loadEnchant(fConfig.getConfigurationSection(id))
            }?: listOf()

        }

        private fun loadEnchant(s: ConfigurationSection?): EmpireEnchantement? {
            s ?: return null
            val _chances = s.getIntegerList("chances")
            val chancec = intArrayOf(
                _chances.getOrNull(0) ?: return null,
                _chances.getOrNull(1) ?: return null,
                _chances.getOrNull(2) ?: return null
            )
            return EmpireEnchantement(
                id = s.name,
                maxLevel = s.getInt("maxLevel", 5),
                expPerLevel = s.getInt("expPerLevel",1),
                chances = chancec,
                value = s.getDouble("value"),
                increaseModifier = s.getDouble("increaseModifier"),
                enchantingTableEnabled = s.getBoolean("enchantingTableEnabled",true),
                anvilEnchantingEnabled = s.getBoolean("anvilEnchantingEnabled",true),
            )
        }
    }
}