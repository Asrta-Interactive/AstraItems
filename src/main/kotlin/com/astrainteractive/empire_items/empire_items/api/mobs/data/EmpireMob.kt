package com.astrainteractive.empire_items.empire_items.api.mobs.data

import com.astrainteractive.empire_items.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType

data class EmpireMob(
    val id: String,
    val entity:String,
    val spawn:SpawnInfo?,
    val attributes: List<EmpireMobAttribute>,
    val onEvent: List<EmpireMobEvent>,
    val potionEffects:List<MobPotionEffect>,
    val canBurn:Boolean,
    val hitDelay:Int,
    val hitRange:Int,
    val decreaseDamageByRange:Boolean,
    val bossBar:MobBossBar?
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
            val events = EmpireMobEvent.get(s.getConfigurationSection("events"))
            return EmpireMob(
                id = s.getString("id") ?: s.name,
                entity = s.getString("entity",EntityType.ZOMBIE.name)!!,
                attributes = attribute,
                onEvent = events,
                spawn = SpawnInfo.fromSection(s.getConfigurationSection("spawn")),
                potionEffects = MobPotionEffect.getAll(s.getConfigurationSection("effects")),
                canBurn = s.getBoolean("canBurn",true),
                hitDelay = s.getInt("hitDelay",0),
                hitRange = s.getInt("hitRange",20),
                decreaseDamageByRange =s.getBoolean("decreaseDamageByRange",false),
                bossBar = MobBossBar.getBar(s.getConfigurationSection("bossBar"))
            )
        }
    }

}