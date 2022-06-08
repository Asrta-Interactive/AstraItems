package com.astrainteractive.empire_items.empire_items.events

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.api.mobs.MobApi.modeledEntity
import com.astrainteractive.empire_items.api.mobs.data.CustomEntityInfo
import com.astrainteractive.empire_items.empire_items.util.playSound
import io.papermc.paper.event.entity.EntityMoveEvent
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.random.Random

class ModelEngineEvent : EventListener {


    val bossBarScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance, Runnable {
        MobApi.bossBars.toMap().forEach bossBarsEntities@{
            val entity = it.key
            val bossBar = it.value

            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.location.distance(entity.location) > 70)
                    bossBar.removePlayer(player)
                else bossBar.addPlayer(player)

            }
        }
    }, 0L, 5L)
    val entitySoundScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance, Runnable {
        MobApi.activeMobs.forEach { mobInfo ->
            if (Random.nextDouble(100.0) < 5)
                mobInfo.entity.location.playSound(mobInfo.empireMob.idleSound[Random.nextInt(mobInfo.empireMob.idleSound.size)])
            MobApi.executeAction(mobInfo,"onTick")
        }
    }, 0L, 20L)


    val onMobSpawn = DSLEvent.event(EntitySpawnEvent::class.java) { e ->
        if (MobApi.isSpawnIgnored(e.location))
            return@event
        val mobs = MobApi.getByNaturalSpawn(e.entity) ?: return@event
        MobApi.replaceEntity(mobs.shuffled().first(), e.entity, naturalSpawn = true)
    }


    val entityMove = DSLEvent.event(EntityMoveEvent::class.java) { e ->
        val modeledEntity = e.entity.modeledEntity ?: return@event
        val activeModel = modeledEntity.activeModel ?: return@event
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return@event
        val event = empireMob.events["onMove"] ?: return@event
        MobApi.executeEvent(e.entity, activeModel, event, "onMove")
    }

    val onEntityTarget = DSLEvent.event(EntityTargetEvent::class.java) { e ->
        val name = e.target?.name?.uppercase()?:return@event
        val modeledEntity = e.entity.modeledEntity ?: return@event
        val activeModel = modeledEntity.activeModel ?: return@event
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return@event
        if (empireMob.ignoreMobs.contains(name))
            e.isCancelled = true
    }

    val onMobDamage = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        val modeledEntity = e.entity.modeledEntity ?: return@event
        val activeModel = modeledEntity.activeModel ?: return@event
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return@event
        val livingEntity = (e.entity as LivingEntity)
        MobApi.bossBars[e.entity]?.let {
            it.progress = livingEntity.health / livingEntity.maxHealth
        }
        val event = empireMob.events["onDamaged"]?.let {
            MobApi.executeEvent(e.entity, activeModel, it, "onDamaged")
            it.addPlayerPotionEffect?.forEach {effect->
                effect.play(e.damager as? LivingEntity)
            }
            Attribute.GENERIC_MAX_HEALTH
        }
        MobApi.executeAction(
            CustomEntityInfo(
                e.entity,
                empireMob,
                modeledEntity, activeModel
            ), "onDamaged"
        )
        if (livingEntity.health - e.damage < 0)
            empireMob.events["onDeath"]?.let {
                MobApi.executeEvent(e.entity, activeModel, it, "onDeath")
            }


    }

    val onDamage = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        if (e.entity !is LivingEntity)
            return@event
        val entityInfo = MobApi.getCustomEntityInfo(e.damager) ?: return@event
        if (MobApi.isAttackAnimationTracked(e.damager) || entityInfo?.empireMob?.hitDelay < 0) {
            MobApi.stopAttackAnimationTrack(e.damager)
            entityInfo.empireMob.events["onDamage"]?.let {
                it.actions
            }
            return@event
        }
        e.isCancelled = true
        MobApi.executeAction(entityInfo, "onDamage")
        MobApi.performAttack(entityInfo, listOf(e.entity), e.damage)
    }

    val onDeath = DSLEvent.event(EntityDeathEvent::class.java) { e ->
        MobApi.deleteEntityBossBar(e.entity)
        val modeledEntity = e.entity.modeledEntity ?: return@event
        val activeModel = modeledEntity.activeModel ?: return@event
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return@event
        val event = empireMob.events["onDeath"] ?: return@event
        MobApi.executeEvent(e.entity, activeModel, event, "onDeath")
    }


    override fun onDisable() {
        bossBarScheduler.cancel()
        entitySoundScheduler.cancel()
    }
}