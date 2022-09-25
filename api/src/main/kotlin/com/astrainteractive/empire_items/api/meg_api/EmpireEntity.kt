package com.astrainteractive.empire_items.api.meg_api

import com.astrainteractive.empire_items.api.meg_api.api.IEmpireActiveModel
import com.astrainteractive.empire_items.api.meg_api.api.IEmpireModeledEntity
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import org.bukkit.entity.Entity

data class EmpireEntity(
    val entity: Entity,
    val ymlMob: YmlMob,
    val modeledEntity: IEmpireModeledEntity,
    val activeModel: IEmpireActiveModel
)
