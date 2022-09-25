package com.astrainteractive.empire_items.api.meg_api

import com.ticxo.modelengine.api.model.ActiveModel

interface IEmpireActiveModel {
    val isAttackAnimationInProgress: Boolean
    val activeModel: ActiveModel

    fun isPlayingAnimation(id: String): Boolean
    fun playAnimation(id: String)


}