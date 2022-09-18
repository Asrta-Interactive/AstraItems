package com.astrainteractive.empire_items.api.model_engine

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.BukkitMain
import com.astrainteractive.empire_items.api.model_engine.ModelEngineApi.playAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import kotlin.math.max

class AttackApi {
    private val currentAttackingMobs = mutableSetOf<Entity>()
    fun onAttack(e: Entity, target: Entity, damage: Double): Boolean {
        if (currentAttackingMobs.contains(e)) return false
        val ignoredMobs = ModelEngineApi.getCustomEntityInfo(e) ?: return false
        performAttack(ignoredMobs, target, damage)
        return true
    }

    fun performAttack(entityInfo: CustomEntityInfo, target: Entity, damage: Double, animation: String = "attack") {

        val modeledEntity = entityInfo.modeledEntity
        val activeModel = entityInfo.activeModel
        val ymlMob = entityInfo.ymlMob
        val e = entityInfo.entity
        AsyncHelper.launch {
            val frame = activeModel.getState(animation)?.frame
            val animationLength = activeModel.getState(animation)?.animationLength
            if (animationLength == null || frame == null || frame < 0f || frame.toInt() >= animationLength) {
                if (currentAttackingMobs.contains(e)) return@launch
                currentAttackingMobs.add(e)
                activeModel.playAnimation(animation)
            } else return@launch
            delay(entityInfo.ymlMob.hitDelay * 1L)
            val distance = e.location.distance(target.location)
            if (distance > ymlMob.hitRange){
                currentAttackingMobs.remove(e)
                return@launch
            }

            val damage = if (ymlMob.decreaseDamageByRange)
                damage / max(1.0, distance)
            else damage
            AsyncHelper.launch(Dispatchers.BukkitMain)  {
                if ((e as LivingEntity).health > 0)
                    (target as LivingEntity).damage(damage, e)
                AsyncHelper.launch {
                    delay(100)
                    currentAttackingMobs.remove(e)
                }
            }
        }

    }
}