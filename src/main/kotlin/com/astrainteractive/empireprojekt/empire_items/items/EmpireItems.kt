package com.astrainteractive.empireprojekt.empire_items.items


import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.astralibs.FileManager
import com.google.gson.reflect.TypeToken
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.ItemsAPI
import com.astrainteractive.empireprojekt.items.data.EmpireItem
import org.bukkit.ChatColor


class EmpireItems {


    private fun getItemsInFile(file: FileManager) = AstraYamlParser.fromYAML<List<EmpireItem>>(
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