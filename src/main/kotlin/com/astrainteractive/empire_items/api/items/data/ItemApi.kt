package com.astrainteractive.empire_items.api.items.data

import com.astrainteractive.empire_items.api.font.FontApi
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.api.upgrade.AstraUpgrade
import com.astrainteractive.empire_items.api.utils.Disableable
import kotlinx.coroutines.delay
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

object ItemApi : Disableable {
    var enabled: Boolean = false
        private set
    private val itemsInfo: MutableList<EmpireItem> = mutableListOf()
    private val itemStacks: MutableList<ItemStack> = mutableListOf()
    private var itemStacksMap: MutableMap<String, ItemStack> = mutableMapOf()
    private var itemsInfoMap: MutableMap<String, EmpireItem> = mutableMapOf()
    private var blockInfoByData: MutableMap<Int, EmpireItem> = mutableMapOf()
    private var recipes: MutableMap<String, MutableList<Recipe>> = mutableMapOf()
    private var upgrades: MutableList<AstraUpgrade> = mutableListOf()

    fun addRecipe(itemID: String, recipe: Recipe) {
        val mutableList = recipes[itemID] ?: mutableListOf()
        mutableList.add(recipe)
        recipes[itemID] = mutableList
    }

    override suspend fun onDisable() {
        itemStacks.clear()
        itemsInfo.clear()
        itemStacksMap.clear()
        itemsInfoMap.clear()
        blockInfoByData.clear()
        recipes.clear()
        upgrades.clear()
        enabled = false
    }

    override suspend fun onEnable() {
        while (!FontApi.enabled)
            delay(100L)
        getCustomItemsFiles()?.map { fileManager ->
            val astraItem = EmpireItem.getItems(fileManager) ?: return@map
            itemsInfo.addAll(astraItem)
        }
        itemsInfoMap = itemsInfo.associateBy { it.id }.toMutableMap()
        itemStacksMap = itemsInfo.associate { Pair(it.id, it.toItemStack()) }.toMutableMap()
        blockInfoByData = itemsInfo.filter { it.block != null }.associateBy { it.block!!.data }.toMutableMap()
        itemStacks.addAll(itemStacksMap.values)
        upgrades = AstraUpgrade.getUpgrades()?.toMutableList() ?: mutableListOf()
        enabled = true

    }

    fun String?.toAstraItemOrItem(amount: Int? = 1): ItemStack? {
        return getItemStack(amount) ?: ItemStack(
            Material.getMaterial(this ?: return null) ?: return null
        )?.apply { setAmount(amount ?: 1) } ?: null
    }

    fun String?.getItemStack(amount: Int? = 1) = getItemStackByID(this, amount)
    fun String?.getItemInfo() = itemsInfoMap[this]
    fun getItemStackByID(id: String?, amount: Int? = 1) = itemStacksMap[id]?.clone()?.apply { setAmount(amount ?: 1) }

    @JvmName("getItemInfoFromString")
    fun getItemInfo(id: String?) = itemsInfoMap[id]
    fun getItemsIDS() = itemsInfoMap.keys.toList()

    fun ItemStack.getAstraID() = getAstraIDByItemStack(this)
    fun getAstraIDByItemStack(itemStack: ItemStack) = itemStack.itemMeta?.getPersistentData(BukkitConstants.ASTRA_ID())

    fun ItemStack.isCustomItem() = isCustomItemByItemStack(this)
    fun isCustomItemByItemStack(itemStack: ItemStack) = getAstraIDByItemStack(itemStack) != null

    fun ItemStack.getItemInfo() = getItemInfoByItemStack(this)
    fun getItemInfoByItemStack(itemStack: ItemStack) = getItemInfo(itemStack.getAstraID())

    fun isAstraBlockByItemStack(itemStack: ItemStack) = getItemInfoByItemStack(itemStack)?.block != null
    fun isMusicDiscByItemStack(itemStack: ItemStack) = getItemInfoByItemStack(itemStack)?.musicDisc != null

    fun getBlockInfoByData(data: Int?) = blockInfoByData[data]

    fun getItemRecipes(id: String) = recipes[id]

    fun getBlocksInfos() = blockInfoByData.values
    fun getSimpleItems() = itemsInfo.toList()


}


