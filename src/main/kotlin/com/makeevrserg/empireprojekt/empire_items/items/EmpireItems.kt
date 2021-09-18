package com.makeevrserg.empireprojekt.empire_items.items

import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.items.data.EmpireItem
import com.makeevrserg.empireprojekt.items.data.block.Block
import com.makeevrserg.empireprojekt.items.data.decoration.Decoration
import com.makeevrserg.empireprojekt.items.data.interact.Interact
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class EmpireItems {
//    val empireItems: MutableMap<String, ItemStack> = mutableMapOf()
//    val empireBlocks: MutableMap<String, Block> = mutableMapOf()
//    val empireDecorations: MutableMap<String, Decoration> = mutableMapOf()
//    val empireBlocksByData: MutableMap<Int, String> = mutableMapOf()
//    val itemsInfo: MutableList<EmpireItem> = mutableListOf()
//    val empireEvents: MutableMap<String, List<Interact>> = mutableMapOf()
//    val empireDiscs: MutableMap<String, EmpireItem> = mutableMapOf()


    private var existedCustomModelData: MutableMap<Material, MutableList<Int>> = mutableMapOf()

    private fun getItemInFile(file: FileManager, itemId: String) = EmpireYamlParser.fromYAML<EmpireItem>(
        file.getConfig(),
        EmpireItem::class.java,
        listOf("yml_items", itemId)
    )

    init {
        val itemsList = mutableListOf<EmpireItem>()
        ItemsAPI.clearLocation()

        for (fileManager: FileManager in EmpirePlugin.empireFiles.empireItemsFiles) {
            println("${ChatColor.YELLOW}${EmpirePlugin.translations.LOADING_FILE}: ${ChatColor.AQUA} ${fileManager.configName}")
            for (itemId in fileManager.getConfig().getConfigurationSection("yml_items")?.getKeys(false) ?: listOf()) {
                val item = getItemInFile(fileManager, itemId) ?: continue
                if (itemsList.contains(item))
                    continue
                itemsList.add(item)
                ItemsAPI.addLocation(itemId,fileManager)
            }
        }


//        for (item in itemsList.toList()) {
//            val itemStack = item.getItemStack() ?: continue
//            empireItems[item.id] = itemStack
//            if (item.block != null) {
//                empireBlocks[item.id] = item.block
//                empireBlocksByData[item.block.data] = item.id
//            }
//            if (item.decoration != null)
//                empireDecorations[item.id] = item.decoration
//
//            if (item.interact != null)
//                empireEvents[item.id] = item.interact
//
//            if (item.musicDisc != null) empireDiscs[item.id] = item
//
//            if (item.customModelData == 0)
//                itemsList.remove(item)
//        }
//        itemsInfo.addAll(itemsList)


    }


}