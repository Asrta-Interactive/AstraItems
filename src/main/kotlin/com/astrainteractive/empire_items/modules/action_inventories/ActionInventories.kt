package com.astrainteractive.empire_items.modules.action_inventories

import com.astrainteractive.empire_items.api.utils.Disableable
import com.astrainteractive.empire_items.modules.action_inventories.data.ActionInventoriesHolder
import com.astrainteractive.empire_items.modules.action_inventories.data.ActionInventory

object ActionInventories:Disableable {
    override suspend fun onEnable() {
        println(ActionInventoriesHolder.getAll())
    }

    override suspend fun onDisable() {
    }
}