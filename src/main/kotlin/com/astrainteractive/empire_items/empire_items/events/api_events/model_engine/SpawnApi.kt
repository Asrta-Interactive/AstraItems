package com.astrainteractive.empire_items.empire_items.events.api_events.model_engine

import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.mobs.CustomEntityInfo
import com.astrainteractive.empire_items.api.mobs.MobApi.activeModel
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.astrainteractive.empire_items.api.utils.addAttribute
import com.astrainteractive.empire_items.api.utils.calcChance
import com.astrainteractive.empire_items.api.utils.getBiome
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

class SpawnApi {
    private val currentSpawnLocations = mutableSetOf<Location>()
    private fun getMobSpawnList(e: Entity): List<YmlMob>? {
        val mobs = EmpireItemsAPI.ymlMobById.values.filter { ymlMob ->
            !ymlMob.spawn?.values?.filter { cond ->
                val chance = cond.replace[e.type.name]
                val c1 = calcChance(chance?.toFloat() ?: -1f)
                val c2 = e.location.y > cond.minY && e.location.y < cond.maxY
                val c3 = if (cond.biomes.isEmpty()) true else cond.biomes.contains(e.location.getBiome().name)
                c1 && c2 && c3
            }.isNullOrEmpty()
        }
        if (mobs.isEmpty()) return null
        return mobs
    }

    fun requestMobSpawn(location: Location, entity: Entity): CustomEntityInfo? {
        if (currentSpawnLocations.contains(location)) return null
        return getMobSpawnList(entity)?.firstOrNull()?.let {
            spawnMob(it, location)
        }
    }

    fun spawnMob(ymlMob: YmlMob, location: Location): CustomEntityInfo? {
        currentSpawnLocations.add(location)
        println(1)
        val spawnedMob = location.world.spawnEntity(location, EntityType.fromName(ymlMob.entity) ?: return null)
        spawnedMob.customName = ymlMob.id
        spawnedMob.isCustomNameVisible = false
        (spawnedMob as? LivingEntity)?.let {
            it.removeWhenFarAway = false
        }
        println(2)
        ymlMob.attributes.forEach { _, ymlAttribute ->
            val attr = valueOfOrNull<Attribute>(ymlAttribute.name) ?: return@forEach
            (spawnedMob as? LivingEntity)?.addAttribute(attr, ymlAttribute.realValue)
        }
        (spawnedMob as? LivingEntity)?.equipment?.clear()
        if (!ymlMob.canBurn) {
            spawnedMob.isVisualFire = false
            spawnedMob.fireTicks = 0
        }
        ymlMob.potionEffects.forEach {
            (spawnedMob as? LivingEntity)?.let { spawnedMob ->
                it.value.copy(duration = Int.MAX_VALUE).play(spawnedMob)
            }
        }
        println(3)

        spawnedMob.isSilent = true
        val model = ModelEngineAPI.api.modelManager.createActiveModel(ymlMob.id)
        val modeledEntity = ModelEngineAPI.api.modelManager.createModeledEntity(spawnedMob)
        modeledEntity.addActiveModel(model)
        modeledEntity.detectPlayers()
        modeledEntity.activeModel?.modelId
        modeledEntity?.isInvisible = true
        val customEntityInfo = CustomEntityInfo(spawnedMob, ymlMob, modeledEntity, model)
        currentSpawnLocations.remove(location)

        println(customEntityInfo)
        return customEntityInfo
    }
}