package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.modeledEntity
import com.astrainteractive.empire_items.empire_items.api.mobs.MobApi.playAnimation
import com.astrainteractive.empire_items.empire_items.api.mobs.data.BoneInfo
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMobEvent
import com.astrainteractive.empire_items.empire_items.util.Cooldown
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.empire_items.util.getBiome
import com.astrainteractive.empire_items.empire_items.util.playSound
import com.destroystokyo.paper.ParticleBuilder
import com.ticxo.modelengine.api.generator.blueprint.Bone
import com.ticxo.modelengine.api.model.ActiveModel
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
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
        val type = e.entityType.name
        val mobs = MobApi.empireMobs.filter { emob ->
            emob.spawn?.conditions?.firstOrNull { cond ->
                val chance = cond.replace[type]
                val c1 = calcChance(chance?.toFloat() ?: -1f)
                val c2 = e.location.y > cond.minY && e.location.y < cond.maxY
                val c3 = if (cond.biomes.isNullOrEmpty()) true else cond.biomes.contains(e.location.getBiome().name)
                c1 && c2 && c3
            } != null
        }
        if (mobs.isNullOrEmpty())
            return
        MobApi.replaceEntity(mobs.shuffled().first(), e.entity)
    }

    private val modelSet = mutableSetOf<Int>()
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
        if (modelSet.contains(e.damager.entityId)) {
            modelSet.remove(e.damager.entityId)
            return
        }
        val modeledEntity = e.damager.modeledEntity ?: return
        val activeModel = modeledEntity.activeModel ?: return
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return
        e.isCancelled = true
        val event = empireMob.onEvent.firstOrNull { it.eventName == "EntityDamageByEntityEvent" } ?: return
        activeModel.playAnimation(event.animation ?: "attack")
        MobApi.runLater(event.hitAfter ?: 0L) {
            val distance = e.damager.location.distance(e.entity.location)
            if (distance > (event.range ?: 5))
                return@runLater
            modelSet.add(e.damager.entityId)
            val damage = if (event.decreaseDamageByRange)
                e.damage / max(1.0, distance)
            else e.damage
            executeEvent(e.damager, activeModel, event)
            (e.entity as LivingEntity).damage(damage, e.damager)
        }

    }

    override fun onDisable() {
    }
}