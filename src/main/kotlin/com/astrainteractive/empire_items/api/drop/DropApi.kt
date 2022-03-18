package com.astrainteractive.empire_items.api.drop

import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.Disableable
import com.astrainteractive.empire_items.empire_items.util.calcChance
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

object DropApi : Disableable {

    private val _dropsMap: MutableList<AstraDrop> = mutableListOf()

    override suspend fun onDisable() {
        _dropsMap.clear()
    }

    override suspend fun onEnable() {
        _dropsMap.addAll(AstraDrop.getDrops().toMutableList())
    }

    fun getDropsFrom(dropFrom: String) = _dropsMap.filter { it.dropFrom == dropFrom }.toSet().toMutableList()
    fun getDropsById(id: String) = _dropsMap.filter { it.id == id }.toSet().toMutableList()


    fun getDrops(list: List<AstraDrop>): List<ItemStack> {
        return list.mapNotNull {
            if (!calcChance(it.chance))
                return@mapNotNull null
            val amount = it.calculatedAmount
            if (amount <= 0)
                return@mapNotNull null
            return@mapNotNull it.id.toAstraItemOrItem(amount)
        }
    }

    fun spawnDrop(dropFrom: String, location: Location?): Boolean {
        var isDropped = false
        getDropsFrom(dropFrom).forEach {
            if (!calcChance(it.chance))
                return@forEach
            val amount = it.calculatedAmount
            if (amount <= 0)
                return@forEach
            val item = it.id.toAstraItemOrItem(amount) ?: return@forEach
            if (location?.world?.dropItemNaturally(location, item) != null)
                isDropped = true
        }
        return isDropped
    }

}