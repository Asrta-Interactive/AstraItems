package com.astrainteractive.empire_items.meg.wrapper.data

import com.astrainteractive.empire_items.meg.wrapper.core.IEmpireActiveModel
import com.astrainteractive.empire_items.meg.wrapper.core.IEmpireModeledEntity
import com.atrainteractive.empire_items.models.mob.YmlMob
import org.bukkit.entity.Entity

data class EmpireEntity(
    val entity: Entity,
    val ymlMob: YmlMob,
    val modeledEntity: IEmpireModeledEntity,
    val activeModel: IEmpireActiveModel
)
