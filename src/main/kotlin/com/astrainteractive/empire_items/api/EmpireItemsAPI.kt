package com.astrainteractive.empire_items.api

import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.IManager
import com.astrainteractive.empire_items.api.utils.getCustomItemsFiles
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.empire_items.util.EmpireSerializer
import com.astrainteractive.empire_items.models.*
import com.astrainteractive.empire_items.models.mob.YmlMob
import com.astrainteractive.empire_items.models.recipies.CraftingTable
import com.astrainteractive.empire_items.models.recipies.Furnace
import com.astrainteractive.empire_items.models.recipies.Shapeless
import com.astrainteractive.empire_items.models.yml_item.YmlItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object EmpireItemsAPI : IManager {
    var itemYamlFiles: List<ItemYamlFile> = listOf()
        private set
    var itemYamlFilesByID: Map<String, YmlItem> = mapOf()
        private set
    var fontByID: Map<String, FontImage> = mapOf()
        private set
    var furnaceRecipeByID: Map<String, Furnace> = mapOf()
        private set
    var shapelessRecipeByID: Map<String, Shapeless> = mapOf()
        private set
    var craftingTableRecipeByID: Map<String, CraftingTable> = mapOf()
        private set
    var dropByID: Map<String, Loot> = mapOf()
        private set
    var dropByDropFrom: Map<String, List<Loot>> = mapOf()
        private set
    var villagerTradeInfoByID: Map<String, VillagerTradeInfo> = mapOf()
        private set
    var villagerTradeInfoByProfession: Map<String, List<VillagerTradeInfo>> = mapOf()
        private set
    var ymlMobById: Map<String, YmlMob> = mapOf()
        private set

    override suspend fun onEnable() {
        itemYamlFiles = getCustomItemsFiles()?.mapNotNull {
            println("File ${it.getFile().name}")
            EmpireSerializer.toClass<ItemYamlFile>(it.getFile())
        } ?: listOf()
        itemYamlFilesByID =
            itemYamlFiles.mapNotNull { it.yml_items?.values?.map { it } }.flatten().associateBy { it.id }

        fontByID = itemYamlFiles.flatMap { it.fontImages.values?.toList()?: emptyList() }.associateBy { it.id }
        furnaceRecipeByID = itemYamlFiles.mapNotNull { it.furnace?.values?.map { it } }.flatten().associateBy { it.id }
        shapelessRecipeByID =
            itemYamlFiles.mapNotNull { it.shapeless?.values?.map { it } }.flatten().associateBy { it.id }
        craftingTableRecipeByID =
            itemYamlFiles.mapNotNull { it.crafting_table?.values?.map { it } }.flatten().associateBy { it.id }
        dropByID =
            itemYamlFiles.mapNotNull { it.loot?.values?.map { it } }.flatten().associateBy { it.id }
        dropByDropFrom = dropByID.values.groupBy { it.dropFrom }
        villagerTradeInfoByID =
            itemYamlFiles.mapNotNull { it.villagerTrades?.values?.map { it } }.flatten().associateBy { it.id }
        villagerTradeInfoByProfession = villagerTradeInfoByID.values.groupBy { it.profession }
        ymlMobById = itemYamlFiles.flatMap { it.ymlMob.values }.associateBy { it.id }
    }

    override suspend fun onDisable() {
    }


    fun toAstraItemOrItemByID(id: String?, amount: Int = 1): ItemStack? = id?.let { id ->
        return@let Material.getMaterial(id)?.let { ItemStack(it, amount) } ?: itemYamlFilesByID[id]?.toItemStack(amount)
    }

    fun String?.toAstraItemOrItem(amount: Int = 1): ItemStack? = toAstraItemOrItemByID(this, amount)
    fun String?.toAstraItem(amount: Int = 1): ItemStack? = itemYamlFilesByID[this]?.toItemStack()
    val ItemStack.empireID: String?
        get() = this.itemMeta?.getPersistentData(BukkitConstants.ASTRA_ID)

}