package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.callSyncMethod
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.modeledEntity
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.playAnimation
import com.astrainteractive.empire_items.empire_items.api.mobs.data.BoneInfo
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMob
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMobEvent
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import com.astrainteractive.empire_items.empire_items.util.Cooldown
import com.astrainteractive.empire_items.empire_items.util.playSound
import com.destroystokyo.paper.ParticleBuilder
import com.ticxo.modelengine.api.generator.blueprint.Bone
import com.ticxo.modelengine.api.model.ActiveModel
import io.papermc.paper.event.entity.EntityMoveEvent
import okhttp3.MultipartBody
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent
import kotlin.math.max

class ModelEngine : IAstraListener {

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
    fun entityMove(e:EntityMoveEvent){
        val modeledEntity = e.entity.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        val event = empireMob.onEvent.firstOrNull { it.eventName == "EntityMoveEvent" } ?: return
        executeEvent(e.entity,activeModel,event)

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
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.entity !is LivingEntity)
            return
        if (isAttacking(e.damager))
            return

        e.isCancelled = true
        performAttack(e.damager, listOf(e.entity), e.damage)
    }

    private val attackingMobs = mutableSetOf<Int>()
    fun setAttacking(entity: Entity) {
        attackingMobs.add(entity.entityId)
    }

    private fun stopAttacking(entity: Entity) = attackingMobs.remove(entity.entityId)
    fun isAttacking(entity: Entity) = attackingMobs.contains(entity.entityId)

    private fun performAttack(damager: Entity, entities: List<Entity>, _damage: Double) {
        AsyncHelper.runBackground {
            val modeledEntity = damager.modeledEntity ?: return@runBackground
            val activeModel = modeledEntity.activeModel ?: return@runBackground
            val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return@runBackground
            val event =
                empireMob.onEvent.firstOrNull { it.eventName == "EntityDamageByEntityEvent" } ?: return@runBackground
            if (isAttacking(damager))
                return@runBackground
            activeModel.playAnimation(event.animation ?: "attack")
            MobApi.runLater(event.hitAfter ?: 0L) {
                entities.forEach { entity ->
                    val distance = damager.location.distance(entity.location)
                    if (distance > (event.range ?: 5))
                        return@runLater
                    setAttacking(damager)
                    val damage = if (event.decreaseDamageByRange)
                        _damage / max(1.0, distance)
                    else _damage

                    callSyncMethod{
                        executeEvent(damager, activeModel, event)
                    }
                    (entity as LivingEntity).damage(damage, damager)
                    MobApi.runLater(2L) {
                        stopAttacking(damager)
                    }
                }
            }
        }

    }


    override fun onDisable() {
    }
}