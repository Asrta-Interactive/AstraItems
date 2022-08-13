package com.astrainteractive.empire_items.api.model_engine

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.astrainteractive.empire_items.api.utils.Cooldown
import com.astrainteractive.empire_items.api.utils.addAttribute
import com.astrainteractive.empire_items.api.utils.calcChance
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

enum class MobEvent {
    ON_DAMAGE, ON_DAMAGED, ON_TICK, ON_SPAWN, ON_KILLED
}

class EventController {
    val eventCooldowns = Cooldown<String>()
    fun YmlMob.YmlMobEvent.MobAction.Condition?.checkCondition(
        entityInfo: CustomEntityInfo,
        id: String,
        actionID: String,
    ): Boolean {
        val condition = this ?: return true
        condition.cooldown?.let { cooldown ->
            if (eventCooldowns.hasCooldown(id + actionID, cooldown)) return false
            eventCooldowns.setCooldown(id)
        }
        (entityInfo.entity as? LivingEntity)?.let {
            if (it.health > (condition.whenHPBelow ?: Int.MAX_VALUE)) return false
        }
        if (!calcChance(condition.chance ?: 100.0)) return false
        if (!condition.animationNames.isNullOrEmpty())
            if (condition.animationNames?.firstOrNull {
                    (entityInfo.activeModel.getState(it)?.frame ?: -1f) > 0f
                }.isNullOrEmpty()) return false
        return true
    }

    fun triggerEvent(mobEvent: MobEvent, entity: Entity, target: Entity? = null) {
        val entityInfo = ModelEngineApi.getCustomEntityInfo(entity) ?:return
        val event = entityInfo.ymlMob.events[mobEvent.name.uppercase()] ?: return
        val id = mobEvent.name + event.id + entity.entityId.toString()
        event.cooldown?.let {
            if (eventCooldowns.hasCooldown(id, event.cooldown)) return
            eventCooldowns.setCooldown(id)
        }
        event.playSound?.play(entity.location)
        event.playPotionEffect.forEach { (_, effect) ->
            AsyncHelper.callSyncMethod { (target as? LivingEntity)?.let(effect::play) }
        }
        event.actions.forEach { (_, action) ->
            if (action.condition.checkCondition(entityInfo, id, action.id)) return@forEach
            val location = entity.location
            AsyncHelper.callSyncMethod {
                ModelEngineApi.blockedMobSpawn(location, true) {
                    action.summonMinions.forEach { (_, minion) ->
                        val entityType = EntityType.fromName(minion.type) ?: kotlin.run {
                            Logger.warn("Entity ${minion.type} not exists")
                            return@forEach
                        }
                        val spawnedMinion = location.world.spawnEntity(location, entityType).apply {
                            (this as? LivingEntity)?.let {
                                minion.potionEffects.forEach { t, effect ->
                                    effect.copy(duration = Int.MAX_VALUE).play(it)
                                }
                                minion.attributes.forEach { _, attr ->
                                    val attribute = valueOfOrNull<Attribute>(it.name) ?: return@forEach
                                    it.addAttribute(attribute, attr.realValue)
                                }
                            }
                        }
                    }
                }
            }

        }
    }

}