package com.astrainteractive.empire_itemss.api

import com.astrainteractive.empire_itemss.api.models_ext.toItemStack
import ru.astrainteractive.astralibs.EmpireSerializer
import com.atrainteractive.empire_items.models.FontImage
import com.atrainteractive.empire_items.models.ItemYamlFile
import com.atrainteractive.empire_items.models.Loot
import com.atrainteractive.empire_items.models.VillagerTradeInfo
import com.atrainteractive.empire_items.models.mob.YmlMob
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import com.atrainteractive.empire_items.models.recipies.Furnace
import com.atrainteractive.empire_items.models.recipies.Shapeless
import com.atrainteractive.empire_items.models.yml_item.YmlItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.utils.AstraLibsExtensions.getPersistentData

class EmpireItemsAPI {
    val itemYamlFiles: List<ItemYamlFile> = getCustomItemsFiles()?.mapNotNull {
        println("File ${it.configFile.name}")
        EmpireSerializer.toClass<ItemYamlFile>(it.configFile)
    } ?: listOf()
    var itemYamlFilesByID: Map<String, YmlItem> = itemYamlFiles.mapNotNull { it.yml_items?.values?.map { it } }.flatten().associateBy { it.id }
    var fontByID: Map<String, FontImage> = itemYamlFiles.flatMap { it.fontImages.values?.toList()?: emptyList() }.associateBy { it.id }
    var furnaceRecipeByID: Map<String, Furnace> = itemYamlFiles.mapNotNull { it.furnace?.values?.map { it } }.flatten().associateBy { it.id }
    var shapelessRecipeByID: Map<String, Shapeless> = itemYamlFiles.mapNotNull { it.shapeless?.values?.map { it } }.flatten().associateBy { it.id }
    var craftingTableRecipeByID: Map<String, CraftingTable> = itemYamlFiles.mapNotNull { it.crafting_table?.values?.map { it } }.flatten().associateBy { it.id }
    var dropByID: Map<String, Loot> = itemYamlFiles.mapNotNull { it.loot?.values?.map { it } }.flatten().associateBy { it.id }
    var dropByDropFrom: Map<String, List<Loot>> = dropByID.values.groupBy { it.dropFrom }
    var dropsByID: Map<String, List<Loot>> = dropByID.values.groupBy { it.id }
    var villagerTradeInfoByID: Map<String, VillagerTradeInfo> = itemYamlFiles.mapNotNull { it.villagerTrades?.values?.map { it } }.flatten().associateBy { it.id }
    var villagerTradeInfoByProfession: Map<String, List<VillagerTradeInfo>> = villagerTradeInfoByID.values.groupBy { it.profession }
    var ymlMobById: Map<String, YmlMob> = itemYamlFiles.flatMap { it.ymlMob.values }.associateBy { it.id }

    fun toAstraItemOrItemByID(id: String?, amount: Int = 1): ItemStack? = id?.let { id ->
        return@let Material.getMaterial(id)?.let { ItemStack(it, amount) } ?: itemYamlFilesByID[id]?.toItemStack(amount)
    }
    fun String?.toAstraItemOrItem(amount: Int = 1): ItemStack? = toAstraItemOrItemByID(this, amount)
    fun String?.toAstraItem(amount: Int = 1): ItemStack? = itemYamlFilesByID[this]?.toItemStack()
}

