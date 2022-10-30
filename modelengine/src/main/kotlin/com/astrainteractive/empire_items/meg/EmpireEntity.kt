package com.astrainteractive.empire_items.meg

import com.astrainteractive.empire_items.meg.api.core.IEmpireActiveModel
import com.astrainteractive.empire_items.meg.api.core.IEmpireModeledEntity
import com.atrainteractive.empire_items.models.mob.YmlMob
import org.bukkit.entity.Entity

data class EmpireEntity(
    val entity: Entity,
    val ymlMob: YmlMob,
    val modeledEntity: IEmpireModeledEntity,
    val activeModel: IEmpireActiveModel
)
