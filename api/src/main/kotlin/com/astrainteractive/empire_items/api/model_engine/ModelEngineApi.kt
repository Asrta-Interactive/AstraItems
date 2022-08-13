package com.astrainteractive.empire_items.api.model_engine

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.randomElementOrNull
import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.astrainteractive.empire_items.api.models.yml_item.Interact
import com.astrainteractive.empire_items.api.utils.*
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

data class CustomEntityInfo(
    val entity: Entity,
    val ymlMob: YmlMob,
    val modeledEntity: ModeledEntity,
    val activeModel: ActiveModel,
)

fun bukkitAsyncTimer(delay: Long = 0L, period: Long = 20L, block: Runnable) =
    Bukkit.getScheduler().runTaskTimerAsynchronously(AstraLibs.instance, block, delay, period)

object ModelEngineApi : IManager {
    val _activeMobs: MutableList<CustomEntityInfo> = mutableListOf()

    fun mobByEntityID(id: Int) = _activeMobs.firstOrNull { it.entity.entityId == id }

    private val spawnApi = SpawnApi()
    private val attackApi = AttackApi()
    private var idleSoundScheduler: BukkitTask? = null
    private val eventController = EventController()
    private val bossBarController = BossBarController()
    fun getCustomEntityInfo(e: Entity): CustomEntityInfo? {
        _activeMobs.firstOrNull { it.entity == e }?.let { return it }
        val id = e.customName ?: return null
        val modeledEntity = e.modeledEntity ?: return null
        val activeModel = modeledEntity.activeModel ?: return null
        val empireMob = EmpireItemsAPI.ymlMobById[id] ?: return null
        return CustomEntityInfo(e, empireMob, modeledEntity, activeModel)
    }

    val ModeledEntity.activeModel: ActiveModel?
        get() = this.allActiveModel?.values?.firstOrNull()
    val Entity.modeledEntity: ModeledEntity?
        get() = ModelEngineAPI.api.modelManager.restoreModeledEntity(this)

    fun onEntityDamaged(entity: Entity) = bossBarController.onEntityDamaged(entity)
    fun triggerEvent(mobEvent: MobEvent, entity: Entity, target: Entity? = null) =
        eventController.triggerEvent(mobEvent, entity, target)

    fun ActiveModel.playAnimation(state: String) {
        addState(state, 1, 1, 1.0)
    }

    fun requestMobSpawn(location: Location, entity: Entity) = spawnApi.requestMobSpawn(location, entity)?.let {
        it.ymlMob.bossBar?.let { bar -> bossBarController.create(it.entity, bar) }
        _activeMobs.add(it)
    }

    fun spawnMob(ymlMob: YmlMob, location: Location) = spawnApi.spawnMob(ymlMob, location)?.let {
        _activeMobs.add(it)
        it.ymlMob.bossBar?.let { bar -> bossBarController.create(it.entity, bar) }
        it
    }

    fun blockedMobSpawn(location: Location, force: Boolean, block: () -> Unit) =
        spawnApi.blockedMobSpawn(location, force, block)

    fun onAttack(e: Entity, target: Entity, damage: Double): Boolean = attackApi.onAttack(e, target, damage)

    fun shouldTargetAt(customMob: Entity, target: Entity?): Boolean? {
        val ignoredMobs = getCustomEntityInfo(customMob)?.ymlMob?.ignoreMobs ?: return null
        return ignoredMobs.contains(target?.type?.name?.uppercase())
    }

    fun removeEntity(e: Entity) {
        val ignoredMobs = _activeMobs.firstOrNull { it.entity == e } ?: return
        _activeMobs.remove(ignoredMobs)
        bossBarController.onEntityDead(e)
    }

    override suspend fun onEnable() {
        getPlugin("ModelEngine") ?: return
        AsyncHelper.callSyncMethod {
            _activeMobs.clear()
            val entities = Bukkit.getWorlds().flatMap { world ->
                world.entities.mapNotNull {
                    return@mapNotNull getCustomEntityInfo(it)
                }
            }
            _activeMobs.addAll(entities)
        }
        idleSoundScheduler = bukkitAsyncTimer {
            _activeMobs.toList().forEach {
                AsyncHelper.launch { triggerEvent(MobEvent.ON_TICK, it.entity) }
                if (calcChance(5))
                    it.ymlMob.idleSound.randomElementOrNull?.let { sound ->
                        Interact.PlaySound(sound).play(it.entity.location)
                    }

            }
        }
        bossBarController.onEnable()
    }

    override suspend fun onDisable() {
        getPlugin("ModelEngine") ?: return
        _activeMobs.forEach {
            it.modeledEntity.removeModel(it.activeModel.modelId)
            it.entity.remove()
        }
        _activeMobs.clear()
        idleSoundScheduler?.cancel()
        bossBarController.onDisable()
    }

}

