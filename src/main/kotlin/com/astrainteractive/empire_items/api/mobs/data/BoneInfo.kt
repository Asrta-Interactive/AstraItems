package com.astrainteractive.empire_items.api.mobs.data

import org.bukkit.configuration.ConfigurationSection

data class BoneInfo(
    val bones: List<String>,
    val particle: ParticleInfo
) {
    companion object {
        fun getBones(s: ConfigurationSection?): List<BoneInfo> {
            return s?.getKeys(false)?.mapNotNull {
                fromSection(s.getConfigurationSection(it))
            } ?: listOf()
        }

        fun fromSection(s: ConfigurationSection?): BoneInfo? {
            s ?: return null
            val listBones = s.getStringList("bones")

            val singleBone = s.getString("bones")
            val bones = if (listBones.isNullOrEmpty()) listOf(singleBone?:return null) else listBones
            return BoneInfo(
                bones,
                ParticleInfo.fromSection(s.getConfigurationSection("particle")) ?: return null
            )
        }
    }
}