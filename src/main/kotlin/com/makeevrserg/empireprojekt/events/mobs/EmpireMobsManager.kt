package com.makeevrserg.empireprojekt.events.mobs

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.items.getHEXString
import org.bukkit.Location

import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class EmpireMobsManager {


    data class ReplaceMobSpawn(
        val type: EntityType,
        val chance: Double,
    ) {
        companion object {
            public fun getMobSpawn(sect: ConfigurationSection): ReplaceMobSpawn? {
                return ReplaceMobSpawn(
                    EntityType.fromName(sect.getString("type") ?: sect.name) ?: return null,
                    sect.getDouble("chance"),
                )

            }

            public fun getMobSpawnMap(sect: ConfigurationSection): Map<EntityType, ReplaceMobSpawn> {
                val map = mutableMapOf<EntityType, ReplaceMobSpawn>()
                for (key in sect.getKeys(false)) {
                    val mob = ReplaceMobSpawn.getMobSpawn(sect.getConfigurationSection(key) ?: continue) ?: continue
                    map[mob.type] = mob
                }
                return map

            }
        }
    }

    data class MobAttribute(
        val attribute: Attribute,
        val min: Double,
        val max: Double
    ) {
        companion object {
            public fun createAttribute(conf: ConfigurationSection): MobAttribute {
                return MobAttribute(
                    Attribute.valueOf(conf.name),
                    conf.getDouble("min", 0.0),
                    conf.getDouble("max", 0.0)
                )
            }

            public fun createAttributes(conf: ConfigurationSection?): MutableList<MobAttribute> {
                conf ?: return mutableListOf()
                val list = mutableListOf<MobAttribute>()
                for (attr in conf.getKeys(false))
                    list.add(createAttribute(conf.getConfigurationSection(attr) ?: continue))
                return list
            }
        }
    }

    data class EmpireMob(
        val id: String,
        val idleAnimation: ItemStack,
        val walkAnimation: ItemStack,
        val attackAnimation: ItemStack,
        val useArmorStand:Boolean = false,
        val smallArmorStand:Boolean = false,
        val displayName: String?,
        val soundIdle:String?,
        val soundDie:String?,
        val soundHurt:String?,
        val ai: EntityType,
        val attributes: List<MobAttribute>,
        val replaceMobSpawn: Map<EntityType, ReplaceMobSpawn>
    ) {
        enum class STATE{
            WALK,ATTACK,IDLE
        }
        companion object {
            public fun create(sect: ConfigurationSection?): EmpireMob? {
                sect ?: return null
                return EmpireMob(
                    sect.getString("id") ?: sect.name ?: return null,
                    EmpirePlugin.empireItems.empireItems[sect.getString("idle_animation")] ?: return null,
                    EmpirePlugin.empireItems.empireItems[sect.getString("walk_animation")] ?: return null,
                    EmpirePlugin.empireItems.empireItems[sect.getString("attack_animation")] ?: return null,
                    sect.getBoolean("use_armor_stand"),
                    sect.getBoolean("small_armor_stand"),
                    sect.getHEXString("display_name"),
                    sect.getString("sound_idle"),
                    sect.getString("sound_die"),
                    sect.getString("sound_hurt"),
                    EntityType.fromName(sect.getString("ai") ?: return null) ?: return null,
                    MobAttribute.createAttributes(sect.getConfigurationSection("attributes")),
                    ReplaceMobSpawn.getMobSpawnMap(sect.getConfigurationSection("replace_mob_spawn") ?: return null)
                )
            }
        }
    }


    private fun initMobs() {
        val config = EmpirePlugin.empireFiles.mobsFile.getConfig()?.getConfigurationSection("mobs") ?: return
        val mobsMap = mutableMapOf<String, EmpireMob>()
        val mobsMapByEntitySpawn = mutableMapOf<EntityType, MutableList<EmpireMob>>()

        for (key in config.getKeys(false)) {
            val mob = EmpireMob.create(config.getConfigurationSection(key))
            if (mob == null) {
                println("${EmpirePlugin.translations.MOB_WRON_PARSE} $key")
                continue
            }
            mobsMap[mob.id] = mob
            for (entity in mob.replaceMobSpawn.keys) {
                if (mobsMapByEntitySpawn[entity] == null)
                    mobsMapByEntitySpawn[entity] = mutableListOf()
                mobsMapByEntitySpawn[entity]!!.add(mob)
            }

        }
        empireMobs = mobsMap
        empireMobsByEntitySpawn = mobsMapByEntitySpawn
    }

    companion object {
        public lateinit var instance: EmpireMobsManager
            private set
        public lateinit var empireMobs: Map<String, EmpireMob>
            private set
        public lateinit var empireMobsByEntitySpawn: Map<EntityType, List<EmpireMob>>
            private set

        val spawnList = mutableListOf<Location>()

    }

    lateinit var _empireMobs: EmpireMobsEvent

    init {
        instance = this
        initMobs()
        if (empireMobs.isNotEmpty())
            _empireMobs = EmpireMobsEvent()
    }

    public fun onDisable() {
        if (empireMobs.isNotEmpty())
            _empireMobs.onDisable()
    }
}