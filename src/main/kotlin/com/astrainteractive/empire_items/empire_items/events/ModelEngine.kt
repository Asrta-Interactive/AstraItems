package com.astrainteractive.empire_items.empire_items.events

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.modeledEntity
import com.astrainteractive.empire_items.empire_items.api.mobs.data.BoneInfo
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMobEvent
import com.astrainteractive.empire_items.empire_items.util.Cooldown
import com.astrainteractive.empire_items.empire_items.util.playSound
import com.destroystokyo.paper.ParticleBuilder
import com.ticxo.modelengine.api.generator.blueprint.Bone
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

    @EventHandler
    fun onDamaged(e: EntityDamageEvent) {
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val event = empireMob.onEvent.firstOrNull { it.eventName == "EntityDamageEvent" } ?: return
        executeEvent(e.entity, activeModel, event)
    }

    @EventHandler
    fun onMobSpawn(e: EntitySpawnEvent) {
        if (MobApi.isSpawnIgnored(e.location))
            return
        val mobs = MobApi.getByNaturalSpawn(e.entity) ?: return
        MobApi.replaceEntity(mobs.shuffled().first(), e.entity)
    }

    @EventHandler
    fun entityMove(e: EntityMoveEvent) {
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val event = empireMob.onEvent.firstOrNull { it.eventName == "EntityMoveEvent" } ?: return
        executeEvent(e.entity, activeModel, event)

    }

    private val particleCooldown: Cooldown<Int> = Cooldown()
    private val soundCooldown: Cooldown<Int> = Cooldown()

    private fun executeEvent(entity: Entity, model: ActiveModel, event: EmpireMobEvent) {
        if (event.cooldown == null)
            entity.location.playSound(event.sound)
        if (soundCooldown.hasCooldown(entity.entityId, event.cooldown)) {
            entity.location.playSound(event.sound)
            soundCooldown.setCooldown(entity.entityId)
        }
        playParticle(entity, model, event.bones)
    }


    private fun playParticle(e: Entity, model: ActiveModel, bonesInfo: List<BoneInfo>) {
        bonesInfo.forEach { boneInfo ->
            if (particleCooldown.hasCooldown(e.entityId, boneInfo.particle.cooldown))
                return
            else particleCooldown.setCooldown(e.entityId)
            var particleBuilder = ParticleBuilder(Particle.valueOf(boneInfo.particle.name))
                .extra(boneInfo.particle.extra)
                .count(boneInfo.particle.amount)
                .force(true)
            particleBuilder = if (boneInfo.particle.color != null)
                particleBuilder.color(boneInfo.particle.color)
            else particleBuilder

            boneInfo.bones.forEach { bones ->
                val l = e.location.clone()
                var modelBone: Bone? = null
                bones.split(".").forEach bon@{ bone ->
                    modelBone = modelBone?.getBone(bone) ?: model.blueprint.getBone(bone)
                    modelBone?.let {
                        l.add(it.localOffsetX, it.localOffsetY, it.localOffsetZ)
                    }
                }
                particleBuilder.location(l).spawn()
            }
        }
    }

    @EventHandler
    fun onMobDamage(e: EntityDamageByEntityEvent) {
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val bar = MobApi.bossBars[e.entity] ?: return
        val livingEntity = (e.entity as LivingEntity)
        bar.progress = livingEntity.health/livingEntity.maxHealth
        Logger.log("${bar.progress}")
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player)
            Logger.log("${(e.entity as LivingEntity).health}")
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
    }
}