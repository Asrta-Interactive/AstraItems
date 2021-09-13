package com.makeevrserg.empireprojekt.empire_items.items

import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.items.data.interact.Sound
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
    val empireItems: MutableMap<String, ItemStack> = mutableMapOf()
    val empireBlocks: MutableMap<String, Block> = mutableMapOf()
    val empireDecorations: MutableMap<String, Decoration> = mutableMapOf()
    val empireBlocksByData: MutableMap<Int, String> = mutableMapOf()
    val itemsInfo: MutableList<EmpireItem> = mutableListOf()
    val empireEvents: MutableMap<String, List<Interact>> = mutableMapOf()
    val empireDiscs: MutableMap<String, EmpireItem> = mutableMapOf()


    private var existedCustomModelData: MutableMap<Material, MutableList<Int>> = mutableMapOf()

    private fun getItemsInFile(file:FileManager) = EmpireYamlParser.fromYAML<List<EmpireItem>>(
        file.getConfig(),
        object : TypeToken<List<EmpireItem?>?>() {}.type,
        listOf("yml_items")
    )

    init {
        val itemsList = mutableListOf<EmpireItem>()

        for (fileManager: FileManager in EmpirePlugin.empireFiles.empireItemsFiles) {
            println("${ChatColor.YELLOW}+${EmpirePlugin.translations.LOADING_FILE}: ${ChatColor.AQUA} ${fileManager.configName}")
            itemsList.addAll(getItemsInFile(fileManager) ?: continue)
        }

        for (item in itemsList.toList()){
            val itemStack = item.getItemStack()?:continue
            empireItems[item.id] = itemStack
            if (item.block!=null) {
                empireBlocks[item.id] = item.block
                empireBlocksByData[item.block.data] = item.id
            }
            if (item.decoration!=null)
                empireDecorations[item.id] = item.decoration

            if (item.interact!=null)
                empireEvents[item.id] = item.interact

            if (item.musicDisc!=null)empireDiscs[item.id] = item

            if (item.customModelData==0)
                itemsList.remove(item)

        }
        itemsInfo.addAll(itemsList)



    }


}