package com.astrainteractive.empire_items.api.mobs.data

import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType

data class EmpireMob(
    val id: String,
    val entity: String,
    val modelId: String,
    val decreaseDamageByRange: Boolean,
    val canBurn: Boolean,
    val idleSound:List<String>,
    val potionEffects: List<MobPotionEffect>,
    val attributes: List<EmpireMobAttribute>,
    val hitDelay: Int,
    val hitRange: Int,
    val spawn: SpawnInfo?,
    val bossBar: MobBossBar?,
    val events: Map<String, EmpireMobEvent>
) {

    companion object {

        fun getAll(): List<EmpireMob> {
            return getCustomItemsFiles()?.mapNotNull {
                val s = it.getConfig().getConfigurationSection("mobs") ?: return@mapNotNull null
                s.getKeys(false).mapNotNull { key ->
                    fromSection(s.getConfigurationSection(key))
                }
            }?.flatten() ?: listOf()
        }

        fun fromSection(s: ConfigurationSection?): EmpireMob? {
            s ?: return null
            val attribute = EmpireMobAttribute.get(s.getConfigurationSection("attributes"))
            val id = s.getString("id") ?: s.name
            return EmpireMob(
                id = id,
                entity = s.getString("entity", EntityType.ZOMBIE.name)!!,
                modelId = s.getString("modelId") ?: id,
                attributes = attribute,
                spawn = SpawnInfo.fromSection(s.getConfigurationSection("spawn")),
                potionEffects = MobPotionEffect.getAll(s.getConfigurationSection("effects")),
                canBurn = s.getBoolean("canBurn", true),
                idleSound  = s.getStringList("idleSound"),
                hitDelay = s.getInt("hitDelay", 0),
                hitRange = s.getInt("hitRange", 20),
                decreaseDamageByRange = s.getBoolean("decreaseDamageByRange", false),
                bossBar = MobBossBar.getBar(s.getConfigurationSection("bossBar")),
                events = EmpireMobEvent.get(s.getConfigurationSection("events")).associateBy { it.parentKey }
            )
        }
    }

}