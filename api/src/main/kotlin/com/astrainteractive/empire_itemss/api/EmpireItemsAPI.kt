package com.astrainteractive.empire_itemss.api

import com.astrainteractive.empire_itemss.api.models_ext.toItemStack
import ru.astrainteractive.astralibs.EmpireSerializer
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import com.astrainteractive.empire_itemss.api.utils.IManager
import com.astrainteractive.empire_itemss.api.utils.getCustomItemsFiles
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

class EmpireItemsAPI : IManager {
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
    var dropsByID: Map<String, List<Loot>> = mapOf()
        private set
    var villagerTradeInfoByID: Map<String, VillagerTradeInfo> = mapOf()
        private set
    var villagerTradeInfoByProfession: Map<String, List<VillagerTradeInfo>> = mapOf()
        private set
    var ymlMobById: Map<String, YmlMob> = mapOf()
        private set

    override fun onEnable() {
        itemYamlFiles = getCustomItemsFiles()?.mapNotNull {
            println("File ${it.configFile.name}")
            EmpireSerializer.toClass<ItemYamlFile>(it.configFile)
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
        dropsByID = dropByID.values.groupBy { it.id }
        villagerTradeInfoByID =
            itemYamlFiles.mapNotNull { it.villagerTrades?.values?.map { it } }.flatten().associateBy { it.id }
        villagerTradeInfoByProfession = villagerTradeInfoByID.values.groupBy { it.profession }
        ymlMobById = itemYamlFiles.flatMap { it.ymlMob.values }.associateBy { it.id }
    }

    override fun onDisable() {
    }


    fun toAstraItemOrItemByID(id: String?, amount: Int = 1): ItemStack? = id?.let { id ->
        return@let Material.getMaterial(id)?.let { ItemStack(it, amount) } ?: itemYamlFilesByID[id]?.toItemStack(amount)
    }

    fun String?.toAstraItemOrItem(amount: Int = 1): ItemStack? = toAstraItemOrItemByID(this, amount)
    fun String?.toAstraItem(amount: Int = 1): ItemStack? = itemYamlFilesByID[this]?.toItemStack()


}

val ItemStack.empireID: String?
    get() = this.itemMeta?.getPersistentData(BukkitConstants.ASTRA_ID)