package com.astrainteractive.empire_items.api.mobs.data

import com.astrainteractive.empire_items.api.YmlItem
import org.bukkit.configuration.ConfigurationSection

data class EmpireMobEvent(
    val parentKey: String,
    val sound: EmpireMobSound?,
    val bones: List<BoneInfo>?,
    val actions: List<MobAction>,
    val addPlayerPotionEffect: List<YmlItem.Interact.PlayPotionEffect>?
) {
    companion object {
        fun get(s: ConfigurationSection?): List<EmpireMobEvent> {
            return s?.getKeys(false)?.mapNotNull { key ->
                fromSection(s.getConfigurationSection(key))
            } ?: listOf()
        }

        fun fromSection(s: ConfigurationSection?): EmpireMobEvent? {
            s ?: return null
            return EmpireMobEvent(
                parentKey = s.name,
                sound = EmpireMobSound.get(s.getConfigurationSection("sound")),
                bones = BoneInfo.getBones(s.getConfigurationSection("bones")),
                actions = MobAction.getAll(s.getConfigurationSection("actions")),
                addPlayerPotionEffect = listOf(YmlItem.Interact.PlayPotionEffect(""))
            )
        }
    }
}
