package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.EventManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantApi

class EnchantManager : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()
    val vampirismManager: Vampirism = Vampirism().apply { onEnable(this@EnchantManager) }
    val frostAspect: FrostAspect = FrostAspect().apply { onEnable(this@EnchantManager) }
    val butcher = Butcher().apply { onEnable(this@EnchantManager) }

    init {
        EmpireEnchantApi.onEnable()
        Logger
    }

    override fun onDisable() {
        super.onDisable()
        EmpireEnchantApi.onDisable()
    }
}