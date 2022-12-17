package com.astrainteractive.empire_items.meg.wrapper.core

import com.ticxo.modelengine.api.model.ActiveModel

interface IEmpireActiveModel {
    val isAttackAnimationInProgress: Boolean
    val activeModel: ActiveModel

    fun isPlayingAnimation(id: String): Boolean
    fun playAnimation(id: String)


}