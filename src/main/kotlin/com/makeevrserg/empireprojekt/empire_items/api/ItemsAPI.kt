package com.makeevrserg.empireprojekt.empire_items.api

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import com.makeevrserg.empireprojekt.empire_items.util.crafting.CraftingManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object ItemsAPI {

    /**
     * Список локаций предметов.
     * Предмет - Файл, в котором он лежит
     */
    private val itemLocation = mutableMapOf<String,FileManager>()
    fun clearLocation() = itemLocation.clear()
    fun addLocation(itemId:String,file:FileManager){
        itemLocation[itemId] = file
    }

    fun getRecipeKey(id: String?): NamespacedKey? {
        id ?: return null
        if (!EmpirePlugin.empireItems.empireItems.containsKey(id))
            return null
        return NamespacedKey(EmpirePlugin.instance, BetterConstants.CUSTOM_RECIPE_KEY.name + id)
    }

    fun useInCraft(item: String): MutableSet<String> {
        val itemStack = EmpirePlugin.empireItems.empireItems[item] ?: ItemStack(
            Material.getMaterial(item) ?: return mutableSetOf()
        )
        val set = mutableSetOf<String>()

        for (itemResult in EmpirePlugin.instance.recipies.keys) {
            val itemRecipies: CraftingManager.EmpireRecipe =
                EmpirePlugin.instance.recipies[itemResult] ?: continue
            for (empireRecipe in itemRecipies.craftingTable)
                if (empireRecipe.ingredientMap.values.contains(itemStack))
                    set.add(itemResult)
            for (empireRecipe in itemRecipies.furnace)
                if (empireRecipe.input == itemStack)
                    set.add(itemResult)
        }
        return set

    }

    fun getItemStackByID(id: String): ItemStack? {
        return EmpirePlugin.empireItems.empireItems[id] ?: ItemStack(Material.getMaterial(id) ?: return null)
    }

    fun getItemStackByName(str: String): ItemStack {
        return EmpirePlugin.empireItems.empireItems[str] ?: ItemStack(Material.getMaterial(str) ?: Material.PAPER)
    }


    private fun getEmpireID(meta: ItemMeta?): String? {
        return meta?.persistentDataContainer?.get(
            BetterConstants.EMPIRE_ID.value,
            PersistentDataType.STRING
        )

    }

    fun getEmpireID(item: ItemStack?): String? {
        item ?: return null
        return getEmpireID(item.itemMeta)
    }

    fun manageWithEmpireDurability(itemStack: ItemStack): ItemStack {

        val itemMeta = itemStack.itemMeta ?: return itemStack
        val damage: Short = itemStack.durability

        val maxCustomDurability: Int = itemMeta.persistentDataContainer.get(
            BetterConstants.MAX_CUSTOM_DURABILITY.value,
            PersistentDataType.INTEGER
        ) ?: return itemStack

        val empireDurability = maxCustomDurability - damage * maxCustomDurability / itemStack.type.maxDurability
        itemMeta.persistentDataContainer.set(
            BetterConstants.EMPIRE_DURABILITY.value,
            PersistentDataType.INTEGER,
            empireDurability
        )
        val d: Int = itemStack.type.maxDurability -
                itemStack.type.maxDurability * empireDurability / maxCustomDurability
        itemStack.durability = d.toShort()
        itemStack.itemMeta = itemMeta
        return itemStack

    }

    fun String?.getEmpireItem(): ItemStack? {
        return EmpirePlugin.empireItems.empireItems[this]
    }


    fun String?.asEmpireItem(): ItemStack? {
        return EmpirePlugin.empireItems.empireItems[this]
    }

    fun String?.asEmpireItemOrItem(): ItemStack? {
        return this.asEmpireItem() ?: ItemStack(Material.getMaterial(this ?: return null) ?: return null)
    }

    @JvmName("getEmpireID1")
    fun ItemStack?.getEmpireID(): String? {
        return getEmpireID(this)
    }
}