package com.astrainteractive.empire_items.api.meg_api

import com.astrainteractive.empire_items.api.models.mob.YmlMob
import org.bukkit.entity.Entity
import java.util.UUID

data class EmpireEntity(
    val entity: Entity,
    val ymlMob: YmlMob,
    val modeledEntity: IEmpireModeledEntity,
    val activeModel: IEmpireActiveModel
)
data class EntityInfo(
    val isSpawning: Boolean = true,
    var isAttacking: Boolean = false,
    val modelID: String,
    val empireID: String,
    val uuid:UUID
)
