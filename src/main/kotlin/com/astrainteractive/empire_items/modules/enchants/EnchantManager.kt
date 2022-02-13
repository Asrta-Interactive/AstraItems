package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.EventManager
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantApi

class EnchantManager : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()
    val vampirismManager: Vampirism = Vampirism().apply { onEnable(this@EnchantManager) }

    init {
        EmpireEnchantApi.onEnable()
    }

    override fun onDisable() {
        super.onDisable()
        EmpireEnchantApi.onDisable()
    }
}