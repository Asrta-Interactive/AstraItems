package com.astrainteractive.empire_items.api.mobs.data

import com.astrainteractive.empire_items.api.items.data.EmpireItem.Companion.getIntOrNull
import com.astrainteractive.empire_items.api.items.data.interact.PlayParticle
import com.astrainteractive.empire_items.api.items.data.interact.PlayPotionEffect
import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType

fun ConfigurationSection?.section(path: String) = this?.getConfigurationSection(path)?.getKeys(false)?.map {
    it to this.getConfigurationSection("$path.$it")!!
}

data class MobAction(
    val id:String,
    val startAfter:Int,
    val condition: Condition,
    val summonProjectile: List<SummonProjectile>,
    val summonMinion: List<Minion>

) {
    data class Condition(
        val whenHPBelow: Int,
        val animationNames: List<String>,
        val cooldown: Int,
        val chance:Double
    )

    data class SummonProjectile(
        val damage: Int,
        val playParticle: PlayParticle
    )

    data class Minion(
        val type: String,
        val amount: Int,
        val attributes: List<EmpireMobAttribute>?,
        val potionEffects: List<PlayPotionEffect>?,
    )

    companion object {
        fun getAll(s: ConfigurationSection?):List<MobAction> {
            s ?: return listOf()
            return s.getKeys(false).mapNotNull { key ->
                val section = s.getConfigurationSection(key) ?: return@mapNotNull null
                val condition = section.getConfigurationSection("condition") ?: return@mapNotNull null
                val summonProjectile = section.section("summonProjectile") ?: listOf()
                val summonMinions = section.section("summonMinions")?: listOf()
                return@mapNotNull MobAction(
                    id = section.getString("id")?:section.name,
                    startAfter = section.getInt("startAfter",0),
                    condition = Condition(
                        whenHPBelow = condition.getInt(Condition::whenHPBelow.name,-1),
                        animationNames = condition.getStringList(Condition::animationNames.name),
                        cooldown = condition.getInt(Condition::cooldown.name, 60000),
                        chance = condition.getDouble("chance",100.0)
                    ),
                    summonProjectile = summonProjectile.mapNotNull { (key, projectile) ->
                        SummonProjectile(
                            damage = projectile.getInt(SummonProjectile::damage.name),
                            playParticle = PlayParticle.getSinglePlayParticle(
                                projectile.getConfigurationSection(
                                    SummonProjectile::playParticle.name
                                )
                            ) ?: return@mapNotNull null
                        )
                    },
                    summonMinion = summonMinions.mapNotNull {(key,minionSection)->
                        Minion(
                            type = minionSection.getString(Minion::type.name)?:return@mapNotNull null,
                            amount = minionSection.getIntOrNull(Minion::amount.name)?:return@mapNotNull null,
                            attributes = EmpireMobAttribute.get(minionSection.getConfigurationSection(Minion::attributes.name)),
                            potionEffects = PlayPotionEffect.getMultiPlayPotionEffect(minionSection.getConfigurationSection(Minion::potionEffects.name)),
                        )
                    }
                )
            }
        }
    }
}

data class EmpireMob(
    val id: String,
    val entity: String,
    val modelId: String,
    val decreaseDamageByRange: Boolean,
    val canBurn: Boolean,
    val idleSound: List<String>,
    val potionEffects: List<PlayPotionEffect>,
    val attributes: List<EmpireMobAttribute>,
    val hitDelay: Int,
    val hitRange: Int,
    val spawn: SpawnInfo?,
    val bossBar: MobBossBar?,
    val ignoreMobs:List<String>,
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
                potionEffects = PlayPotionEffect.getMultiPlayPotionEffect(s.getConfigurationSection("potionEffects"))?: listOf(),
                canBurn = s.getBoolean("canBurn", true),
                idleSound = s.getStringList("idleSound"),
                hitDelay = s.getInt("hitDelay", 0),
                hitRange = s.getInt("hitRange", 20),
                decreaseDamageByRange = s.getBoolean("decreaseDamageByRange", false),
                bossBar = MobBossBar.getBar(s.getConfigurationSection("bossBar")),
                events = EmpireMobEvent.get(s.getConfigurationSection("events")).associateBy { it.parentKey },
                ignoreMobs = s.getStringList("ignoreMobs")
            )
        }
    }

}