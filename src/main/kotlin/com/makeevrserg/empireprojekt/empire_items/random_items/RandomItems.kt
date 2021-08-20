package makeevrserg.empireprojekt.random_items

import makeevrserg.empireprojekt.random_items.data.RandomItem
import org.bukkit.inventory.ItemStack

class RandomItems {
    private val randomItems = RandomItem.new()
    private lateinit var mapItems:Map<String,RandomItem>
    init {
        getMapItems()
    }

    public fun getList() = mapItems.keys.toList()
    public fun getItem(key:String):ItemStack?{
        return mapItems[key]?.build()
    }

    private fun getMapItems() {
        val map = mutableMapOf<String, RandomItem>()
        for (item in randomItems ?: listOf())
            map[item.id] = item
        mapItems = map
    }
}