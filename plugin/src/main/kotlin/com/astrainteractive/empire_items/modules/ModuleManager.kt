package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.enchants.EnchantManager
import com.astrainteractive.empire_itemss.api.utils.IManager

object ModuleManager: IManager {

    private var enchantManager: EnchantManager? = null
    override suspend fun onEnable() {
        enchantManager = EnchantManager(EnchantsModule.value)
    }

    override suspend fun onDisable() {
        enchantManager?.onDisable()
    }
}