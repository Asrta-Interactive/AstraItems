package com.astrainteractive.empire_items.modules.action_inventories.data

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.empire_items.api.items.data.interact.PlayCommand
@kotlinx.serialization.Serializable
data class ActionInventoriesHolder(
    val action_inventory:Map<String,ActionInventory>
){


    companion object {
        fun getAll(): ActionInventoriesHolder? {
            val s = FileManager("modules/action_inventory.yml").getConfig()
            return AstraYamlParser.configurationSectionToClass<ActionInventoriesHolder>(s)
        }
    }
}
@kotlinx.serialization.Serializable
data class ActionInventory(
    val title: String,
    val items: Map<String, ActionInventoryItem>
) {
    @kotlinx.serialization.Serializable
    data class ActionInventoryItem(
        val name: String?,
        val description: List<String>?,
        val copyInto: List<Int>,
        val index: Int,
        val type: String,
        val customModelData: Int?,
        val enumAction: String?,
        val action: ActionInventoryAction
    ) {
        @kotlinx.serialization.Serializable
        data class ActionInventoryAction(
            val commands: Map<String, PlayCommand>,
            val conditions: ActionInventoryActionCondition
        ) {
            @kotlinx.serialization.Serializable
            data class ActionInventoryActionCondition(
                val placedItems: Map<String, PlacedItem>
            ) {
                @kotlinx.serialization.Serializable
                data class PlacedItem(
                    val item: String,
                    val amount: Int,
                    val index: Int
                )
            }
        }
    }
}