package com.astrainteractive.empire_items.api.mobs

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.utils.Cooldown
import com.astrainteractive.empire_items.api.utils.IManager
import com.astrainteractive.empire_items.empire_items.util.*
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.destroystokyo.paper.ParticleBuilder
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import kotlinx.coroutines.*
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import java.util.*
import kotlin.math.max

data class CustomEntityInfo(
    val entity: Entity,
    val ymlMob: YmlMob,
    val modeledEntity: ModeledEntity,
    val activeModel: ActiveModel
)

object BossBarManager {

    val bossBars = mutableMapOf<Entity, BossBar>()

    fun bossBarKey(id: Int) = NamespacedKey(EmpirePlugin.instance, "esmp$id")
    fun createEntityBossBar(e: Entity, id: String, bossBar: YmlMob.YmlMobBossBar) {
        val key = bossBarKey(e.entityId)
        val barColor = valueOfOrNull<BarColor>(bossBar.color) ?: BarColor.RED
        val barStyle = valueOfOrNull<BarStyle>(bossBar.barStyle) ?: BarStyle.SOLID
        val barFlags = bossBar.flags.mapNotNull { valueOfOrNull<BarFlag>(it) }.toTypedArray()
        val bar =
            Bukkit.createBossBar(key, convertHex(bossBar.name), barColor, barStyle, *barFlags)
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

    fun deleteBossBars() {
        bossBars.toMap().forEach { deleteEntityBossBar(it.key) }
    }
}

object MobApi : IManager {
    private val _activeMobs: MutableList<CustomEntityInfo> = mutableListOf()
    val activeMobs: List<CustomEntityInfo>
        get() = _activeMobs
    private val ignoredSpawn = mutableSetOf<Location>()

    /**
     * Play animation for selected [ActiveModel]
     */
    fun ActiveModel.playAnimation(state: String) {
        addState(state, 1, 1, 1.0)
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
     * @param [e] - Naturally spawned entity
     * @return list of models, which can replace current entity
     */
    fun getByNaturalSpawn(e: Entity): List<YmlMob>? {
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


    /**
     * Replace entity [e] with [EmpireMob] as Custom ModelEngine entity
     */
    fun replaceEntity(eMob: YmlMob, e: Entity, naturalSpawn: Boolean = false): CustomEntityInfo? {
        if (naturalSpawn) {
            val loc = e.location.clone()
            e.remove()
            spawnMob(eMob, loc)
            return null
        }
        eMob.attributes.forEach {
            val attr = valueOfOrNull<Attribute>(it.value.name) ?: return@forEach
            val amount = it.value.realValue
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
            BossBarManager.createEntityBossBar(e, eMob.id, bossBar)
        }
        (e as LivingEntity).equipment?.clear()

        if (!eMob.canBurn) {
            e.isVisualFire = false
            e.fireTicks = 0
            //(e as LivingEntity).equipment?.setHelmet(ItemStack(Material.BARRIER), true)
        }
        eMob.potionEffects.forEach { effect ->
            effect.value.copy(duration = Int.MAX_VALUE).play(e)
        }
        e.isSilent = true
        val model = ModelEngineAPI.api.modelManager.createActiveModel(eMob.id)
        val modeledEntity = ModelEngineAPI.api.modelManager.createModeledEntity(e)
        modeledEntity.addActiveModel(model)
        modeledEntity.detectPlayers()
        modeledEntity.activeModel?.modelId
        modeledEntity?.isInvisible = true
        val customEntityInfo = CustomEntityInfo(e, eMob, modeledEntity, model)
        _activeMobs.add(customEntityInfo)
        return customEntityInfo
    }


    /**
     * Remove active entity from list
     */
    fun removeActiveEntity(entity: Entity) {
        _activeMobs.removeIf { it.entity == entity }
    }

    /**
     * Get all active entites as List of [CustomEntityInfo]
     */
    fun getActiveEntities() = _activeMobs.toList()
    fun getActiveEntity(e: Entity) = _activeMobs.firstOrNull() { it.entity == e }

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
    fun spawnMob(eMob: YmlMob, l: Location): CustomEntityInfo? {
        ignoreSpawnForLocation(l)
        val mob = l.world.spawnEntity(l, EntityType.fromName(eMob.entity) ?: return null)
        mob.customName = eMob.id
        mob.isCustomNameVisible = false
        (mob as? LivingEntity)?.let {
            it.removeWhenFarAway = false
        }
        return replaceEntity(eMob, mob)
    }

    private val attackingMobs = mutableSetOf<Int>()
    private fun setAttackAnimationTrack(entity: Entity) = synchronized(MobApi) { attackingMobs.add(entity.entityId) }
    fun stopAttackAnimationTrack(entity: Entity) = synchronized(MobApi) { attackingMobs.remove(entity.entityId) }
    fun isAttackAnimationTracked(entity: Entity) = synchronized(MobApi) { attackingMobs.contains(entity.entityId) }

    /**
     * @return [CustomEntityInfo]
     */
    fun getCustomEntityInfo(e: Entity): CustomEntityInfo? {
        val id = e.customName ?: return null
        val modeledEntity = e.modeledEntity ?: return null
        val activeModel = modeledEntity.activeModel ?: return null
        val empireMob = EmpireItemsAPI.ymlMobById[id] ?: return null
        return CustomEntityInfo(e, empireMob, modeledEntity, activeModel)
    }

    /**
     * Perform attack for custom entity
     */
    fun performAttack(entityInfo: CustomEntityInfo?, entities: List<Entity>, _damage: Double) {
        entityInfo ?: return
        val modeledEntity = entityInfo.modeledEntity
        val activeModel = entityInfo.activeModel
        val empireMob = entityInfo.ymlMob
        val damager = entityInfo.entity
        AsyncHelper.launch {
            val frame = activeModel.getState("attack")?.frame
            val animationLength = activeModel.getState("attack")?.animationLength
            if (animationLength == null || frame == null || frame < 0f || frame.toInt() >= animationLength) {
                synchronized(MobApi) {
                    if (isAttackAnimationTracked(damager))
                        return@launch
                    setAttackAnimationTrack(damager)
                }
                activeModel.playAnimation("attack")
            } else return@launch
            AsyncHelper.launch {
                delay(empireMob.hitDelay * 1L)
                entities.forEach { entity ->
                    val distance = damager.location.distance(entity.location)
                    if (distance > empireMob.hitRange)
                        return@forEach
                    val damage = if (empireMob.decreaseDamageByRange)
                        _damage / max(1.0, distance)
                    else _damage

                    empireMob.events["onDamage"]?.let { event ->
                        executeEvent(entity, activeModel, event, "onDamage")
                        event.playPotionEffect.forEach { effect ->
                            entities.forEach { effect.value.play(it as? LivingEntity) }
                        }
                    }
                    AsyncHelper.callSyncMethod {
                        if ((damager as LivingEntity).health > 0)
                            (entity as LivingEntity).damage(damage, damager)
                    }
                }
            }
        }

    }

    private val particleCooldown: Cooldown<Int> = Cooldown()

    private fun playParticle(e: Entity, model: ActiveModel, bonesInfo: List<YmlMob.YmlMobEvent.BoneParticle>?) {
        bonesInfo?.forEach { boneInfo ->
            if (particleCooldown.hasCooldown(e.entityId, boneInfo.cooldown))
                return
            else particleCooldown.setCooldown(e.entityId)
            var particleBuilder = ParticleBuilder(Particle.valueOf(boneInfo.particle.name))
                .extra(boneInfo.particle.extra)
                .count(boneInfo.particle.count)
                .force(true)
            particleBuilder = if (boneInfo.particle.color != null)
                particleBuilder.color(boneInfo.particle.realColor)
            else particleBuilder

            boneInfo.bones.forEach { bones ->
                val l = e.location.clone()
                var modelBone: BlueprintBone? = null
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


    val eventTimers: Cooldown<String> = Cooldown()

    fun executeEvent(entity: Entity, model: ActiveModel, event: YmlMob.YmlMobEvent, eventId: String) {
        val hasSoundCooldown = eventTimers.hasCooldown("${eventId}S${entity.hashCode()}", event.playSound?.cooldown)
        if (event.playSound?.cooldown == null || event.playSound.cooldown == 0 || !hasSoundCooldown) {
            eventTimers.setCooldown("${eventId}S${entity.hashCode()}")
            event.playSound?.play(entity.location)
        }
        playParticle(entity, model, event.boneParticle.values.toList())
    }

    fun executeAction(entityInfo: CustomEntityInfo, event: String) {
        val actions = entityInfo.ymlMob.events[event] ?: return
        actions.actions.forEach { (key, action) ->
            AsyncHelper.launch {
                delay(action.startAfter * 1L)
                val condition = action.condition
                // Check conditions
                (entityInfo.entity as? LivingEntity)?.let {
                    if (it.health > (condition?.whenHPBelow ?: Int.MAX_VALUE))
                        return@launch
                }
                if (eventTimers.hasCooldown(
                        "${action.id}_${entityInfo.entity.hashCode()}}",
                        action.condition?.cooldown
                    )
                ) {
                    return@launch
                } else
                    eventTimers.setCooldown("${action.id}_${entityInfo.entity.hashCode()}}")
                if (!calcChance(condition?.chance ?: 100.0)) return@launch
                if (!condition?.animationNames.isNullOrEmpty()) {
                    val isRightAnimation = condition?.animationNames?.mapNotNull {
                        entityInfo.activeModel.getState(it)?.frame
                    }?.any { it > 0 }
                    if (!(isRightAnimation ?: false)) {
                        return@launch
                    }
                }
                // Summon minions
                action.summonMinions.forEach { (key, summonMinion) ->
                    val entityType = EntityType.fromName(summonMinion.type) ?: kotlin.run {
                        Logger.warn("Entity ${summonMinion.type} not exists")
                        return@forEach
                    }
                    val location = entityInfo.entity.location
                    ignoreSpawnForLocation(location)
                    for (i in 0 until summonMinion.amount)
                        AsyncHelper.callSyncMethod {
                            val spawnedEntity = location.world.spawnEntity(location, entityType).apply {
                                val entity = (this as? LivingEntity) ?: return@apply
                                summonMinion.potionEffects.forEach { (key, _effect) ->
                                    val effect = _effect.copy(duration = Int.MAX_VALUE)
                                    AsyncHelper.callSyncMethod {
                                        effect.play(entity)
                                    }
                                }
                                summonMinion.attributes.forEach { (key, it) ->
                                    val attr = valueOfOrNull<Attribute>(it.name) ?: return@forEach
                                    val amount = it.realValue
                                    entity.registerAttribute(attr)
                                    entity.getAttribute(attr)!!.addModifier(
                                        AttributeModifier(
                                            UUID.randomUUID(),
                                            attr.name,
                                            amount,
                                            AttributeModifier.Operation.ADD_NUMBER
                                        )
                                    )
                                    if (attr == Attribute.GENERIC_MAX_HEALTH)
                                        entity.health = amount
                                }
                            }
                        }

                }
                // Summon projectiles
                action.summonProjectile.forEach { summonProjectile ->

                }

            }

        }

    }

    override suspend fun onEnable() {
        Bukkit.getBossBars().forEach {
            if(it.key.key.contains("esmp"))
                it.removeAll()
        }
        if (Bukkit.getServer().pluginManager.getPlugin("ModelEngine") == null) return
        AsyncHelper.callSyncMethod {
            val entities = Bukkit.getWorlds().flatMap { world ->
                world.entities.mapNotNull {
                    return@mapNotNull getCustomEntityInfo(it)
                }
            }
            _activeMobs.addAll(entities)
        }
    }

    override suspend fun onDisable() {
        if (Bukkit.getServer().pluginManager.getPlugin("ModelEngine") == null) return
        _activeMobs.forEach {
            it.modeledEntity.removeModel(it.activeModel.modelId)
            it.entity.remove()
        }
        _activeMobs.clear()
        BossBarManager.deleteBossBars()
    }

}

