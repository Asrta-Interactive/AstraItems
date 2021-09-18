package com.makeevrserg.empireprojekt.empire_items.items


import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import com.makeevrserg.empireprojekt.items.data.EmpireItem
import com.makeevrserg.empireprojekt.items.data.block.Block
import com.makeevrserg.empireprojekt.items.data.decoration.Decoration
import com.makeevrserg.empireprojekt.items.data.interact.Interact
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.instrument.Instrumentation


class EmpireItems {


    private fun getItemsInFile(file:FileManager) = EmpireYamlParser.fromYAML<List<EmpireItem>>(
        file.getConfig(),
        object : TypeToken<List<EmpireItem?>?>() {}.type,
        listOf("yml_items")
    )

    init {
        val itemsList = mutableListOf<EmpireItem>()
        val ids = mutableListOf<String>()
        ItemsAPI.clear()

        for (fileManager: FileManager in EmpirePlugin.empireFiles.empireItemsFiles) {
            println("${ChatColor.YELLOW}${EmpirePlugin.translations.LOADING_FILE}: ${ChatColor.AQUA} ${fileManager.configName}")
            itemsList.addAll(getItemsInFile(fileManager)?:continue)
//            val map = EmpireYamlParser.getMap(fileManager.getConfig())
//            for (itemId in fileManager.getConfig().getConfigurationSection("yml_items")?.getKeys(false) ?: listOf()) {
//                if (ids.contains(itemId))
//                    continue
//                ids.add(itemId)
//                val item = EmpireYamlParser.fromExistedMap<EmpireItem>(map,EmpireItem::class.java, listOf("yml_items",itemId))?:continue
//
//                itemsList.add(item)
//            }
        }
        ItemsAPI.init(itemsList)


    }


}