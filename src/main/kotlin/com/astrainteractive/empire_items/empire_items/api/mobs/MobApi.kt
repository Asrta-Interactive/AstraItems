package com.astrainteractive.empire_items.empire_items.api.mobs

import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.mobs.data.CustomEntityInfo
import com.astrainteractive.empire_items.empire_items.api.mobs.data.EmpireMob
import com.astrainteractive.empire_items.empire_items.api.mobs.data.MobBossBar
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import com.astrainteractive.empire_items.empire_items.util.Disableable
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.empire_items.util.getBiome
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.math.max

object MobApi : Disableable {

    private var empireMobs: MutableList<EmpireMob> = mutableListOf()
    private var empireMobsById: Map<String, EmpireMob> = mapOf()

    private val activeMobs: MutableList<CustomEntityInfo> = mutableListOf()

    /**
     * @return list of all ids of [EmpireMob]
     */
    fun getEmpireMobsList() = empireMobs.map { it.id }

    private val ignoredSpawn = mutableSetOf<Location>()

    /**
     * Get [EmpireMob] by its id
     * @return [EmpireMob]
     */
    fun getEmpireMob(id: String) = empireMobsById[id]

    fun getModelEngineMobs() = ModelEngineAPI.api.modelManager.modelRegistry.registeredModel.keys

    /**
     * Play animation for selected [ActiveModel]
     */
    fun ActiveModel.playAnimation(state: String) {
        addState(state, 1, 1, 1.0)
    }

    fun runLater(time: Long, block: () -> Unit) {
        Bukkit.getScheduler().runTaskLater(EmpirePlugin.instance, Runnable(block), time)
    }


    /**
     * @return [ModeledEntity]
     */
    val Entity.modeledEntity: ModeledEntity?
        get() = ModelEngineAPI.api.modelManager.restoreModeledEntity(this)

    /**
     * @returm [ActiveModel]
     */
    val ModeledEntity.activeModel: ActiveModel?
        get() = this.allActiveModel?.values?.firstOrNull()

    /**
     * @return id of [ModeledEntity]
     */
    val ModeledEntity.modelId: String?
        get() = this.activeModel?.modelId


    /**
     * @param [e] - Naturally spawned entity
     * @return list of models, which can replace current entity
     */
    fun getByNaturalSpawn(e: Entity): List<EmpireMob>? {
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

    val bossBars = mutableMapOf<Entity, BossBar>()

    fun bossBarKey(id: Int) = NamespacedKey(EmpirePlugin.instance, id.toString())
    fun createEntityBossBar(e: Entity, id: String, bossBar: MobBossBar) {
        val key = bossBarKey(e.entityId)
        val barColor = valueOfOrNull<BarColor>(bossBar.color) ?: BarColor.RED
        val barStyle = valueOfOrNull<BarStyle>(bossBar.barStyle) ?: BarStyle.SOLID
        val barFlags = bossBar.flags.mapNotNull { valueOfOrNull<BarFlag>(it) }.toTypedArray()
        println(barFlags.toList())
        val bar =
            Bukkit.createBossBar(key, bossBar.name, barColor, barStyle, *barFlags)
        bar.isVisible = true
        bar.progress = 1.0
        bossBars[e] = bar
    }

    fun deleteEntityBossBar(e: Entity) {
        val bar = bossBars.remove(e) ?: return
        bar.isVisible = false
        Bukkit.getOnlinePlayers().forEach { bar.removePlayer(it) }
        bar.removeAll()
    }

    /**
     * Replace entity [e] with [EmpireMob] as Custom ModelEngine entity
     */
    fun replaceEntity(eMob: EmpireMob, e: Entity): CustomEntityInfo {
        eMob.attributes.forEach {
            val attr = valueOfOrNull<Attribute>(it.attribute) ?: return@forEach
            val amount = it.amount
            (e as LivingEntity).registerAttribute(attr)
            (e as LivingEntity).getAttribute(attr)!!.addModifier(
                AttributeModifier(
                    UUID.randomUUID(),
                    attr.name,
                    amount,
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
            if (attr == Attribute.GENERIC_MAX_HEALTH)
                (e as LivingEntity).health = amount

        }
        eMob.bossBar?.let { bossBar ->
            createEntityBossBar(e, eMob.id, bossBar)

        }
        (e as LivingEntity).equipment?.clear()

        if (!eMob.canBurn) {
            e.isVisualFire = false
            e.fireTicks = 0
            (e as LivingEntity).equipment?.setHelmet(ItemStack(Material.BARRIER), true)
        }
        eMob.potionEffects.forEach { effect ->
            val potionEffect = PotionEffectType.getByName(effect.effect) ?: return@forEach

            (e as LivingEntity).addPotionEffect(
                PotionEffect(potionEffect, effect.duration, effect.level, false, false, false)
            )
        }
        e.isSilent = true
        val model = ModelEngineAPI.api.modelManager.createActiveModel(eMob.id)
        val modeledEntity = ModelEngineAPI.api.modelManager.createModeledEntity(e)
        modeledEntity.addActiveModel(model)
        modeledEntity.detectPlayers()
        modeledEntity.activeModel?.modelId
        modeledEntity?.isInvisible = true
        val customEntityInfo = CustomEntityInfo(e, eMob, modeledEntity, model)
        activeMobs.add(customEntityInfo)
        return customEntityInfo
    }


    /**
     * Remove active entity from list
     */
    fun removeActiveEntity(entity: Entity) {
        activeMobs.removeIf { it.entity==entity }
    }

    /**
     * Get all active entites as List of [CustomEntityInfo]
     */
    fun getActiveEntities() = activeMobs.toList()
    fun getActiveEntity(e:Entity) = activeMobs.firstOrNull() { it.entity==e }

    /**
     * Should spawned entity in location [l] be ignored
     * @return true/false
     */
    fun isSpawnIgnored(l: Location): Boolean {
        if (ignoredSpawn.contains(l)) {
            ignoredSpawn.remove(l)
            return true
        }
        return false
    }

    /**
     * Ignore spawn event for location [l]
     */
    private fun ignoreSpawnForLocation(l: Location) {
        ignoredSpawn.add(l)
    }

    /**
     * Spawn an [EmpireMob] in location [l]
     * @return [CustomEntityInfo]
     */
    fun spawnMob(eMob: EmpireMob, l: Location): CustomEntityInfo? {
        ignoreSpawnForLocation(l)
        val mob = l.world.spawnEntity(l, EntityType.fromName(eMob.entity) ?: return null)
        return replaceEntity(eMob, mob)
    }

    private val attackingMobs = mutableSetOf<Int>()
    fun setAttacking(entity: Entity) {
        attackingMobs.add(entity.entityId)
    }

    private fun stopAttacking(entity: Entity) = attackingMobs.remove(entity.entityId)
    fun isAttacking(entity: Entity) = attackingMobs.contains(entity.entityId)

    /**
     * @return [CustomEntityInfo]
     */
    fun getCustomEntityInfo(e: Entity): CustomEntityInfo? {
        val modeledEntity = e.modeledEntity ?: return null
        val activeModel = modeledEntity.activeModel ?: return null
        val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return null
        return CustomEntityInfo(e, empireMob, modeledEntity, activeModel)
    }

    /**
     * Perform attack for custom entity
     */
    fun performAttack(damager: Entity, entities: List<Entity>, _damage: Double) {
        AsyncHelper.runBackground {
            val modeledEntity = damager.modeledEntity ?: return@runBackground
            val activeModel = modeledEntity.activeModel ?: return@runBackground
            val empireMob = MobApi.getEmpireMob(activeModel.modelId) ?: return@runBackground
            val event =
                empireMob.onEvent.firstOrNull { it.eventName == "EntityDamageByEntityEvent" } ?: return@runBackground
            if (isAttacking(damager))
                return@runBackground
            val frame = activeModel.getState(event.animation ?: "attack")?.frame
            val animationLength = activeModel.getState(event.animation ?: "attack")?.animationLength
            if (frame == null || frame == 0f || (animationLength != null && frame.toInt() == animationLength))
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

//                    com.astrainteractive.astralibs.async.AsyncHelper.callSyncMethod {
//                        executeEvent(damager, activeModel, event)
//                    }
                    if ((entity as LivingEntity).health > 0)
                        (entity as LivingEntity).damage(damage, damager)
                    MobApi.runLater(2L) {
                        stopAttacking(damager)
                    }
                }
            }
        }

    }


    override fun onEnable() {
        empireMobs = EmpireMob.getAll().toMutableList()
        empireMobsById = empireMobs.associateBy { it.id }
    }

    override fun onDisable() {
        activeMobs.clear()
        empireMobs.clear()
        bossBars.toMap().forEach { deleteEntityBossBar(it.key) }
    }

}

