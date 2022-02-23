package com.astrainteractive.empire_items.api.mobs.data

import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.entity.Entity

data class CustomEntityInfo(
    val entity:Entity,
    val empireMob:EmpireMob,
    val modeledEntity:ModeledEntity,
    val activeModel: ActiveModel
)