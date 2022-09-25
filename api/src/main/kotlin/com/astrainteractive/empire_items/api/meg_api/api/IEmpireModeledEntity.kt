package com.astrainteractive.empire_items.api.meg_api.api

import com.ticxo.modelengine.api.model.ModeledEntity

interface IEmpireModeledEntity {
    val modeledEntity: ModeledEntity
    var isBaseEntityVisible: Boolean
    fun addModel(model: IEmpireActiveModel)
}
