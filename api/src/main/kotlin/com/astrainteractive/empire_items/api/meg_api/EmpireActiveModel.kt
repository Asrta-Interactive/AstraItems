package com.astrainteractive.empire_items.api.meg_api

import com.astrainteractive.empire_items.api.meg_api.api.IEmpireActiveModel
import com.ticxo.modelengine.api.model.ActiveModel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.inventory.meta.BookMeta

class EmpireActiveModel(override val activeModel: ActiveModel) : IEmpireActiveModel {

    override fun isPlayingAnimation(id: String): Boolean {
        return activeModel.animationHandler.isPlayingAnimation(id)
    }

    override val isAttackAnimationInProgress: Boolean
        get() = isPlayingAnimation("attack")

    override fun playAnimation(id: String) {
        activeModel.animationHandler.playAnimation(id, 0.0, 0.0, 1.0, true)
    }

}
