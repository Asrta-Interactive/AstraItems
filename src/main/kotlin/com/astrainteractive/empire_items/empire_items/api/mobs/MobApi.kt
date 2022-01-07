package com.astrainteractive.empire_items.empire_items.api.mobs

import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMob
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.empire_items.util.getBiome
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import java.util.*

object MobApi {

    private var empireMobs: MutableList<EmpireMob> = mutableListOf()
    private var empireMobsById: Map<String, EmpireMob> = mapOf()
    fun loadEmpireMobs() {
        empireMobs.clear()
        empireMobs = EmpireMob.getAll().toMutableList()
        empireMobsById = empireMobs.associateBy { it.id }
    }

    fun getEmpireMobsList() = empireMobs.map { it.id }

    private val ignoredSpawn = mutableSetOf<Location>()
    fun String.getEmpireMob() = empireMobsById[this]

    @JvmName("getEmpireMobByName")
    fun getEmpireMob(id: String) = id.getEmpireMob()

    fun getModelEngineMobs() = ModelEngineAPI.api.modelManager.modelRegistry.registeredModel.keys
    fun ActiveModel.playAnimation(state: String) {
        MobApi.playAnimation(this, state)
    }

    fun runLater(time: Long, block: () -> Unit) {
        Bukkit.getScheduler().runTaskLater(EmpirePlugin.instance, Runnable(block), time)
    }

    @JvmName("playAnimationModel")
    fun playAnimation(model: ActiveModel, state: String) {
        model.addState(state, 1, 1, 1.0)
    }


    val Entity.modeledEntity: ModeledEntity?
        get() = ModelEngineAPI.api.modelManager.restoreModeledEntity(this)
    val ModeledEntity.activeModel: ActiveModel?
        get() = this.allActiveModel?.values?.firstOrNull()
    val ModeledEntity.modelId: String?
        get() = this.activeModel?.modelId


    /**
     * @param enity Naturally spawned entity
     * @return list of models, which can replace current entity
     */
    fun getByNaturalSpawn(e:Entity): List<EmpireMob>? {
        val mobs = MobApi.empireMobs.filter { emob ->
            emob.spawn?.conditions?.firstOrNull { cond ->
                val chance = cond.replace[e.type.name]
                val c1 = calcChance(chance?.toFloat() ?: -1f)
                val c2 = e.location.y > cond.minY && e.location.y < cond.maxY
                val c3 = if (cond.biomes.isNullOrEmpty()) true else cond.biomes.contains(e.location.getBiome().name)
                c1 && c2 && c3
            } != null
        }
        if (mobs.isNullOrEmpty())
            return null
        return mobs
    }



    fun replaceEntity(eMob: EmpireMob, e: Entity): ModeledEntity? {
        eMob.attributes.forEach {
            val attr = valueOfOrNull<Attribute>(it.attribute) ?: return@forEach
            (e as LivingEntity).registerAttribute(attr)
            (e as LivingEntity).getAttribute(attr)?.addModifier(
                AttributeModifier(
                    UUID.randomUUID(),
                    attr.name,
                    it.amount,
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
        }
        e.isSilent = true
        val model = ModelEngineAPI.api.modelManager.createActiveModel(eMob.id)
        val modeledEntity = ModelEngineAPI.api.modelManager.createModeledEntity(e)
        modeledEntity.addActiveModel(model)
        modeledEntity.detectPlayers()
        modeledEntity.activeModel?.modelId
        modeledEntity?.isInvisible = true
        return modeledEntity
    }

    fun isSpawnIgnored(l: Location): Boolean {
        if (ignoredSpawn.contains(l)){
            ignoredSpawn.remove(l)
            return true
        }
        return false
    }
    private fun ignoreSpawnForLocation(l:Location){
        ignoredSpawn.add(l)
    }
    fun spawnMob(eMob: EmpireMob, l: Location): ModeledEntity? {
        ignoreSpawnForLocation(l)
        val mob = l.world.spawnEntity(l, EntityType.fromName(eMob.entity) ?: return null)
        return replaceEntity(eMob, mob)
    }

}

