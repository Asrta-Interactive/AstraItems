package com.makeevrserg.empireprojekt.items

import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.essentials.MusicDiscsEvent
import com.makeevrserg.empireprojekt.items.data.EmpireItem
import com.makeevrserg.empireprojekt.items.data.block.Block
import com.makeevrserg.empireprojekt.items.data.interact.Interact
import empirelibs.EmpireYamlParser
import empirelibs.FileManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class EmpireItems {
    val empireItems: MutableMap<String, ItemStack> = mutableMapOf()
    public val empireBlocks: MutableMap<String, Block> = mutableMapOf()
    public val empireBlocksByData: MutableMap<Int, String> = mutableMapOf()
    public val itemsInfo: MutableList<EmpireItem> = mutableListOf()
    public val empireEvents: MutableMap<String, List<Interact>> = mutableMapOf()
    public val empireDiscsEvent: MutableMap<String, MusicDiscsEvent.MusicDisc> = mutableMapOf()


    private var existedCustomModelData: MutableMap<Material, MutableList<Int>> = mutableMapOf()

    private fun getItemsInFile(file:FileManager) = EmpireYamlParser.parseYamlConfig<List<EmpireItem>>(
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

        for (item in itemsList){
            val itemStack = item.getItemStack()?:continue
            empireItems[item.id] = itemStack
            if (item.block!=null) {
                empireBlocks[item.id] = item.block
                empireBlocksByData[item.block.data] = item.id
            }
            if (item.interact!=null)
                empireEvents[item.id] = item.interact

        }
        itemsInfo.addAll(itemsList)



    }


}