package com.astrainteractive.empire_items.modules.enchants.data

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.empire_items.models.yml_item.Interact
import com.astrainteractive.empire_items.models.yml_item.YmlItem
import org.bukkit.configuration.ConfigurationSection

data class PlayerPotionEnchant(
    val effects: List<Interact.PlayPotionEffect>
) {
    companion object {
        fun fromSection(s: ConfigurationSection?): PlayerPotionEnchant? {
            return PlayerPotionEnchant(
                effects = listOf()
            )
        }
    }
}

data class EmpireEnchantment(
    val id: String,
    val name:String?,
    val maxLevel: Int = 5,
    val expPerLevel: Int = 1,
    val chances: IntArray,
    val value: Double,
    val increaseModifier: Double,
    val enchantingTableEnabled: Boolean = true,
    val anvilEnchantingEnabled: Boolean = true,
) {
    val totalMultiplier: Double
        get() = value * increaseModifier

    companion object {
        private val file: FileManager
            get() = FileManager("modules/empire_enchants.yml")

        fun loadALl(): List<EmpireEnchantment> {
            val config = file.getConfig().getConfigurationSection("enchants")
            return config?.getKeys(false)?.mapNotNull { id ->
                loadEnchant(config.getConfigurationSection(id))
            } ?: listOf()

        }

        private fun loadEnchant(s: ConfigurationSection?): EmpireEnchantment? {
            s ?: return null
            val _chances = s.getIntegerList(EmpireEnchantment::chances.name)
            val chances = intArrayOf(
                _chances.getOrNull(0) ?: return null,
                _chances.getOrNull(1) ?: return null,
                _chances.getOrNull(2) ?: return null
            )

            return EmpireEnchantment(
                id = s.name,
                name = s.getString(EmpireEnchantment::name.name),
                maxLevel = s.getInt(EmpireEnchantment::maxLevel.name, 5),
                expPerLevel = s.getInt(EmpireEnchantment::expPerLevel.name, 1),
                chances = chances,
                value = s.getDouble(EmpireEnchantment::value.name, 1.0),
                increaseModifier = s.getDouble(EmpireEnchantment::increaseModifier.name, 0.0),
                enchantingTableEnabled = s.getBoolean(EmpireEnchantment::enchantingTableEnabled.name, true),
                anvilEnchantingEnabled = s.getBoolean(EmpireEnchantment::anvilEnchantingEnabled.name, true),
            )
        }
    }
}