package com.astrainteractive.empire_items.empire_items.events.api_events.model_engine

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.mobs.CustomEntityInfo
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.api.mobs.MobApi.getCustomEntityInfo
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.astrainteractive.empire_items.api.utils.*
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ActiveModel
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

object ModelEngineApi : IManager {
    private val _activeMobs: MutableList<CustomEntityInfo> = mutableListOf()

    private val spawnApi = SpawnApi()
    private val attackApi = AttackApi()
    fun ActiveModel.playAnimation(state: String) {
        addState(state, 1, 1, 1.0)
    }

    fun requestMobSpawn(location: Location, entity: Entity) = spawnApi.requestMobSpawn(location, entity)?.let {
        _activeMobs.add(it)
    }

    fun spawnMob(ymlMob: YmlMob, location: Location) = spawnApi.spawnMob(ymlMob, location)?.let {
        _activeMobs.add(it)
    }

    fun onAttack(e: Entity, target: Entity, damage: Double):Boolean = attackApi.onAttack(e, target, damage)

    fun shouldTargetAt(customMob: Entity, target: Entity?): Boolean? {
        val ignoredMobs = getCustomEntityInfo(customMob)?.ymlMob?.ignoreMobs ?: return null
        return ignoredMobs.contains(target?.type?.name?.uppercase())
    }

    fun removeEntity(e: Entity) {
        val ignoredMobs = getCustomEntityInfo(e) ?: return

    }

    override suspend fun onEnable() {
        getPlugin("ModelEngine") ?: return
        AsyncHelper.callSyncMethod {
            _activeMobs.clear()
            val entities = Bukkit.getWorlds().flatMap { world ->
                world.entities.mapNotNull {
                    return@mapNotNull MobApi.getCustomEntityInfo(it)
                }
            }
            _activeMobs.addAll(entities)
        }
    }

    override suspend fun onDisable() {
        getPlugin("ModelEngine") ?: return
        _activeMobs.forEach {
            it.modeledEntity.removeModel(it.activeModel.modelId)
            it.entity.remove()
        }
        _activeMobs.clear()
    }

}

