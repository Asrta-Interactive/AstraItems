package com.astrainteractive.empire_items.models

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.empire_items.util.calcChance
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Loot(
    val id: String,
    val dropFrom: String,
    val minAmount: Int = 1,
    val maxAmount: Int = 2,
    val chance: Double = 0.2
) {
    fun generateItem(): ItemStack? {
        if (!calcChance(chance)) return null
        if (minAmount > maxAmount) {
            Logger.warn(
                "Wrong min: ${minAmount} and max: ${maxAmount} amounts of drop id: ${id}; dropFrom: ${dropFrom} ",
                "Loot"
            )
            return null
        }
        val amount = if (minAmount == maxAmount) minAmount else Random.nextInt(minAmount, maxAmount+1)
        return id.toAstraItemOrItem(amount)
    }

    fun performDrop(location: Location) {
        generateItem()?.let {
            location.world.dropItemNaturally(location, it)
        }

    }
}
