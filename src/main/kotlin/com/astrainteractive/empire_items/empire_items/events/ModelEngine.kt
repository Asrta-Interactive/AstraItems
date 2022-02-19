package com.astrainteractive.empire_items.empire_items.events

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.modeledEntity
import com.astrainteractive.empire_items.empire_items.api.mobs.data.BoneInfo
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMobEvent
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import com.astrainteractive.empire_items.empire_items.util.Cooldown
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.empire_items.util.playSound
import com.destroystokyo.paper.ParticleBuilder
import com.ticxo.modelengine.api.model.ActiveModel
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent

class ModelEngine : EventListener {


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
    }, 0L, 20L)
    val entitySoundScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance, Runnable {
        MobApi.activeMobs.forEach { mobInfo ->
            if (calcChance(1))
                mobInfo.entity.location.playSound(mobInfo.empireMob.sound)
        }
    }, 0L, 20L)

    @EventHandler
    fun onDamaged(e: EntityDamageEvent) {
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val event = empireMob.onEvent.firstOrNull { it.eventName == "EntityDamageEvent" } ?: return
        MobApi.executeEvent(e.entity, activeModel, event)
    }

    @EventHandler
    fun onMobSpawn(e: EntitySpawnEvent) {
        if (MobApi.isSpawnIgnored(e.location))
            return
        val mobs = MobApi.getByNaturalSpawn(e.entity) ?: return
        MobApi.replaceEntity(mobs.shuffled().first(), e.entity, naturalSpawn = true)
    }


    @EventHandler
    fun entityMove(e: EntityMoveEvent) {
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val event = empireMob.onEvent.firstOrNull { it.eventName == "EntityMoveEvent" } ?: return
        MobApi.executeEvent(e.entity, activeModel, event)

    }


    @EventHandler
    fun onMobDamage(e: EntityDamageByEntityEvent) {
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val bar = MobApi.bossBars[e.entity] ?: return
        val livingEntity = (e.entity as LivingEntity)
        bar.progress = livingEntity.health / livingEntity.maxHealth
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.entity !is LivingEntity)
            return
        if (MobApi.isAttacking(e.damager))
            return
//        MobApi.getCustomEntityInfo(e.entity)?:return
        e.damager.modeledEntity?.activeModel ?: return

        e.isCancelled = true
        MobApi.performAttack(e.damager, listOf(e.entity), e.damage)
    }

    @EventHandler
    fun onDeath(e: EntityDeathEvent) {
        MobApi.deleteEntityBossBar(e.entity)
    }


    override fun onDisable() {
        bossBarScheduler.cancel()
        entitySoundScheduler.cancel()
    }
}