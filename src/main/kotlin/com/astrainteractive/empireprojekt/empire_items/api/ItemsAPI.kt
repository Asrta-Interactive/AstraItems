package com.astrainteractive.empireprojekt.empire_items.api

import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.BetterConstants
import com.astrainteractive.empireprojekt.empire_items.util.crafting.CraftingManager
import com.astrainteractive.empireprojekt.items.data.EmpireItem
import com.astrainteractive.empireprojekt.items.data.block.Block
import com.astrainteractive.empireprojekt.items.data.decoration.Decoration
import com.astrainteractive.empireprojekt.items.data.interact.Interact
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object ItemsAPI {


    private var empireItemStackById: MutableMap<String, ItemStack> = mutableMapOf()
    private var empireBlockInfoById: MutableMap<String, Block> = mutableMapOf()
    private var empireDecorationInfoById: MutableMap<String, Decoration> = mutableMapOf()
    private var empireBlockIdByData: MutableMap<Int, String> = mutableMapOf()
    private var empireItemsInfoById: MutableMap<String, EmpireItem> = mutableMapOf()
    private var empireEventsById: MutableMap<String, List<Interact>> = mutableMapOf()
    private var empireDiscsById: MutableMap<String, EmpireItem> = mutableMapOf()


    fun clear(){
        empireItemStackById.clear()
        empireBlockInfoById.clear()
        empireDecorationInfoById.clear()
        empireBlockIdByData.clear()
        empireItemsInfoById.clear()
        empireEventsById.clear()
        empireDiscsById.clear()
    }
    fun init(empireItemsInfo:List<EmpireItem>){
        empireItemStackById = empireItemsInfo.filter { it.getItemStack()!=null }.associate { Pair(it.id,it.getItemStack()!!) }.toMutableMap()
        empireBlockInfoById = empireItemsInfo.filter { it.block!=null }.associate { Pair(it.id,it.block!!) }.toMutableMap()
        empireDecorationInfoById = empireItemsInfo.filter { it.decoration!=null }.associate { Pair(it.id,it.decoration!!) }.toMutableMap()
        empireBlockIdByData = empireItemsInfo.filter { it.block!=null }.associate { Pair(it.block!!.data,it.id) }.toMutableMap()
        empireItemsInfoById = empireItemsInfo.associateBy { it.id }.toMutableMap()
        empireEventsById = empireItemsInfo.filter { it.interact!=null }.associate { Pair(it.id,it.interact!!) }.toMutableMap()
        empireDiscsById = empireItemsInfo.filter { it.musicDisc!=null }.associateBy { it.id }.toMutableMap()
    }


    fun getEmpireItemStacks() = empireItemStackById
    fun getEmpireItemsInfo() = empireItemsInfoById
    fun getEmpireBlocks() = empireBlockInfoById
    fun getEmpireBlockIdByData(data:Int) = empireBlockIdByData[data]
    fun getEventByItemId(id:String) = empireEventsById[id]
    fun getEmpireItemStack(id: String?): ItemStack? {
        return empireItemStackById[id ?: return null]
    }

    fun getEmpireItemStackOrItemStack(id: String): ItemStack? {
        return empireItemStackById[id] ?: ItemStack(Material.getMaterial(id) ?: return null)
    }

    fun getEmpireItemInfo(id: String) = empireItemsInfoById[id]
    fun getEmpireBlockInfoById(id: String) = empireBlockInfoById[id]
    fun isEmpireItem(id: String) = empireItemsInfoById.containsKey(id)


    fun String?.getEmpireItem() = getEmpireItemStack(this)
    fun String?.asEmpireItem() = getEmpireItemStack(this)

    fun String?.asEmpireItemOrItem(): ItemStack? {
        return this.asEmpireItem() ?: ItemStack(Material.getMaterial(this ?: return null) ?: return null)
    }

    @JvmName("getEmpireID1")
    fun ItemStack?.getEmpireID(): String? {
        return getEmpireID(this)
    }


    fun getRecipeKey(id: String?): NamespacedKey? {
        id ?: return null
        if (isEmpireItem(id))
            return null
        return NamespacedKey(EmpirePlugin.instance, BetterConstants.CUSTOM_RECIPE_KEY.name + id)
    }

    fun useInCraft(item: String): MutableSet<String> {
        val set = mutableSetOf<String>()
        val itemStack = getEmpireItemStackOrItemStack(item) ?: return set

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

}