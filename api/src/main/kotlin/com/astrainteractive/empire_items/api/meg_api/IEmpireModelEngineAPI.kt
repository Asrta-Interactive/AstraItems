package com.astrainteractive.empire_items.api.meg_api

import com.astrainteractive.empire_items.api.models.mob.YmlMob
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageByEntityEvent

interface IEmpireModelEngineAPI {
    fun spawnMob(ymlMob: YmlMob, location: Location): EmpireEntity
    fun replaceEntity(entity: Entity, vararg ymlMobs: YmlMob): EmpireEntity
    fun createActiveModel(id: String): IEmpireActiveModel
    fun createModeledEntity(entity: Entity): IEmpireModeledEntity

    fun getEmpireEntity(entity: Entity): EmpireEntity?

    fun performAttack(event: EntityDamageByEntityEvent)

}