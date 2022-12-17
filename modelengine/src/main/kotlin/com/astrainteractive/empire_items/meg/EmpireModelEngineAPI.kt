package com.astrainteractive.empire_items.meg

import com.astrainteractive.empire_items.meg.wrapper.EmpireActiveModel
import com.astrainteractive.empire_items.meg.wrapper.EmpireModeledEntity
import com.astrainteractive.empire_items.meg.wrapper.core.IEmpireActiveModel
import com.astrainteractive.empire_items.meg.wrapper.core.IEmpireModeledEntity
import com.astrainteractive.empire_items.meg.wrapper.data.EmpireEntity
import com.astrainteractive.empire_items.meg.wrapper.data.EntityInfo
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.addAttribute
import com.astrainteractive.empire_itemss.api.calcChance
import com.astrainteractive.empire_itemss.api.getBiome
import com.astrainteractive.empire_itemss.api.models_ext.play
import com.atrainteractive.empire_items.models.mob.YmlMob
import com.ticxo.modelengine.api.ModelEngineAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.scheduler.BukkitTask
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.valueOfOrNull
import kotlin.math.max


class EmpireModelEngineAPI(
    _empireItemsApi: IDependency<EmpireItemsAPI>,
    _bossBarController: IDependency<BossBarController>
) {
    private val empireItemsApi by _empireItemsApi
    private val bossBarController by _bossBarController
    val entityTagHolder = TagHolder<Entity, EntityInfo>()
    private val ignoredLocations = HashSet<Location>()

    private fun getMobSpawnList(e: Entity): List<YmlMob>? {
        val mobs = empireItemsApi.ymlMobById.values.filter { ymlMob ->
            !ymlMob.spawn?.values?.filter { cond ->
                val chance = cond.replace[e.type.name]
                calcChance(chance?.toFloat() ?: -1f) &&
                        (e.location.y > cond.minY && e.location.y < cond.maxY) &&
                        (cond.biomes.isEmpty() || cond.biomes.contains(e.location.getBiome().name))
            }.isNullOrEmpty()
        }
        if (mobs.isEmpty()) return null
        return mobs
    }

    private fun configureEntity(entity: Entity, ymlMob: YmlMob) = entity.apply {
        customName = ymlMob.id
        isCustomNameVisible = false

        (this as? LivingEntity)?.let {
            it.removeWhenFarAway = false
            equipment?.clear()
            ymlMob.attributes.forEach { (_, ymlAttribute) ->
                val attr = valueOfOrNull<Attribute>(ymlAttribute.name) ?: return@forEach
                addAttribute(attr, ymlAttribute.realValue)
            }
            val maxHealth = it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: it.maxHealth
            health = maxHealth
            ymlMob.potionEffects.forEach {
                it.value.copy(duration = Int.MAX_VALUE).play(this)
            }
        }

        if (!ymlMob.canBurn) {
            isVisualFire = false
            fireTicks = 0
        }
        isSilent = true
    }

    fun createActiveModel(id: String): IEmpireActiveModel {
        val activeModel = ModelEngineAPI.createActiveModel(id)
        return EmpireActiveModel(activeModel)
    }

    fun createModeledEntity(entity: Entity): IEmpireModeledEntity {
        val modeledEntity = ModelEngineAPI.createModeledEntity(entity)
        return EmpireModeledEntity(modeledEntity)
    }

    fun replaceEntity(entity: Entity, vararg ymlMobs: YmlMob): EmpireEntity {
        val ymlMob = ymlMobs.randomOrNull() ?: throw Exception("Replace entity list must not be empty")
        val entityInfo = EntityInfo(
            modelID = ymlMob.modelID,
            empireID = ymlMob.id,
            uuid = entity.uniqueId
        )
        entityTagHolder.put(entity, entityInfo)
        val model = createActiveModel(ymlMob.id)
        val modeledEntity = createModeledEntity(entity).apply {
            addModel(model)
            isBaseEntityVisible = false
        }
        ymlMob.bossBar?.let { bossBarController.create(entity, it) }
        return EmpireEntity(entity, ymlMob, modeledEntity, model)
    }

    fun processSpawnedMob(location: Location, entity: Entity) {
        if (ignoredLocations.contains(location)) return
        val ymlMon = getMobSpawnList(entity)?.firstOrNull() ?: return
        entity.remove()
        spawnMob(ymlMon, location)
    }

    fun spawnMob(ymlMob: YmlMob, location: Location): EmpireEntity {
        ignoredLocations.add(location)
        val entityType = EntityType.fromName(ymlMob.entity) ?: throw Exception("Unknown entity type: ${ymlMob.entity}")
        println("EntityType: $entityType")
        val entity =
            location.world.spawnEntity(location, entityType).run { configureEntity(this, ymlMob) }
        val replaced = replaceEntity(entity, ymlMob)
        ignoredLocations.remove(location)
        return replaced
    }

    fun performAttack(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity
        val damage = event.damage

        val entityInfo = entityTagHolder.get(damager) ?: return

        if (entityInfo.isAttacking) return
        else {
            entityTagHolder.update(damager) {
                entityInfo.apply { isAttacking = true }
            }
            event.isCancelled = true
        }

        val empireEntity = getEmpireEntity(damager) ?: return
        empireEntity.activeModel.playAnimation("attack")


        val distance = damager.location.distance(entity.location)

        PluginScope.launch(Dispatchers.IO) {
            delay(empireEntity.ymlMob.hitDelay.toLong())

            val calculatedDamage = if (empireEntity.ymlMob.decreaseDamageByRange)
                damage / max(1.0, distance)
            else damage

            PluginScope.launch(Dispatchers.BukkitMain) {
                if ((damager as LivingEntity).health > 0)
                    (entity as LivingEntity).damage(calculatedDamage, damager)
                empireEntity.ymlMob.events["onDamage"]?.playSound?.play(damager.location)
            }

            delay(1000L)

            entityTagHolder.update(damager) {
                entityInfo.apply { isAttacking = false }
            }
        }

    }

    fun getEmpireEntity(entity: Entity): EmpireEntity? = kotlin.runCatching {
        val entityInfo = entityTagHolder.get(entity) ?: return null
        val ymlMob = empireItemsApi.ymlMobById[entityInfo.empireID] ?: return null
        val modeledEntity = ModelEngineAPI.getModeledEntity(entity.uniqueId) ?: return null
        val activeModel = modeledEntity?.getModel(entityInfo.modelID ?: return null) ?: return null
        val empireModeledEntity = EmpireModeledEntity(modeledEntity)
        val empireActiveModel = EmpireActiveModel(activeModel)
        return EmpireEntity(
            entity = entity,
            ymlMob = ymlMob,
            modeledEntity = empireModeledEntity,
            activeModel = empireActiveModel
        )
    }.getOrNull()

    fun onEntityDied(entity: Entity) {
        entityTagHolder.remove(entity)
    }

    fun onEntityTarget(event: EntityTargetEvent) {
        val targetName = event.target?.type?.name?.uppercase() ?: return
        val ymlMob = getEmpireEntity(event.entity)?.ymlMob ?: return
        if (ymlMob.ignoreMobs.contains(targetName))
            event.isCancelled = true

    }

    private var bossBarScheduler: BukkitTask? =
        Bukkit.getScheduler().runTaskTimerAsynchronously(AstraLibs.instance, Runnable {
            PluginScope.launch(Dispatchers.IO) {
                bossBarController.empireMobsBossBars.toList().forEach { bar ->
                    Bukkit.getOnlinePlayers().forEach { player ->
                        val uuid = kotlin.runCatching { bossBarController.entityUUIDFromBossBar(bar) }.getOrNull()
                            ?: return@forEach
                        val entity = withContext(Dispatchers.BukkitMain) { Bukkit.getEntity(uuid) } ?: return@forEach
                        if (!entity.isValid) return@forEach
                        val health = (entity as? LivingEntity)?.health ?: 0.0
                        val isAlive = health >= 0.0
                        val entityInfo = getEmpireEntity(entity) ?: return@forEach
                        if (player.location.world != entityInfo.entity.location.world || !isAlive)
                            bar.removePlayer(player)
                        else if (player.location.distance(entityInfo.entity.location) > 70)
                            bar.removePlayer(player)
                        else bar.addPlayer(player)
                    }
                }
            }

        }, 0L, 20L)

    fun clear() {
        val players = Bukkit.getOnlinePlayers()
        bossBarScheduler?.cancel()
        entityTagHolder.map.forEach { (entity, entityInfo) ->
            getEmpireEntity(entity)?.let { empireEntity ->
                players.forEach {
                    empireEntity.activeModel.activeModel.hideFromPlayer(it)
                    empireEntity.modeledEntity.modeledEntity.hideFromPlayer(it)
                }
                empireEntity.modeledEntity.modeledEntity.removeModel(entityInfo.modelID)
                empireEntity.modeledEntity.modeledEntity.destroy()
                empireEntity.activeModel.activeModel.destroy()
            }
            entity.remove()
        }

    }
}

