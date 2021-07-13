package com.makeevrserg.empireprojekt.items

import com.makeevrserg.empireprojekt.events.empireevents.Gun
import com.makeevrserg.empireprojekt.ESSENTIALS.MusicDiscs
import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import com.makeevrserg.empireprojekt.util.files.FileManager
import org.bukkit.Material

class EmpireItems {
    private var _empireItems: MutableMap<String, ItemStack> = mutableMapOf()
    private var _empireBlocks: MutableMap<String, ItemStack> = mutableMapOf()
    private var _itemsInfo: MutableList<ItemInfo> = mutableListOf()
    private var _empireEvents: MutableMap<String, List<Event>> = mutableMapOf()

    val empireItems: MutableMap<String, ItemStack>
        get() = _empireItems
    val empireEvents: MutableMap<String, List<Event>>
        get() = _empireEvents
    private val _empireGuns: MutableMap<String, EmpireGun> = mutableMapOf()
    val empireGuns: MutableMap<String, EmpireGun>
        get() = _empireGuns

    private val _empireDiscs: MutableMap<String, MusicDiscs.MusicDisc> = mutableMapOf()
    val empireDiscs: MutableMap<String, MusicDiscs.MusicDisc>
        get() = _empireDiscs

    val itemsInfo: MutableList<ItemInfo>
        get() = _itemsInfo

    private var existedCustomModelData: MutableMap<Material, MutableList<Int>> = mutableMapOf()

    data class ItemInfo(
        val id: String,
        val namespace: String,
        val material: String,
        val customModelData: Int,
        var texture_path: String?,
        val model_path: String?
    ) {
        //val permission: String = "empireitems.$id"
    }


    init {


        for (empireFile: FileManager in EmpirePlugin.empireFiles.empireItemsFiles) {

            //println(Gson().toJson(empireFile.getConfig()?.saveToString()))

            val empireFileConfig = empireFile.getConfig() ?: continue//file.yml
            if (!empireFileConfig.contains("yml_items"))
                continue
            for (itemID: String in empireFileConfig.getConfigurationSection("yml_items")!!.getKeys(false)) {
                val itemConfig: ConfigurationSection =
                    empireFileConfig.getConfigurationSection("yml_items")!!.getConfigurationSection(itemID)!!

                val item = EmpireItem(itemConfig)
                val itemStack = item.getItemStack() ?: continue

                if (existedCustomModelData.containsKey(item.material)) {
                    if (existedCustomModelData[item.material]!!.contains(item.customModelData))
                        println("Уже существует custommodeldata ${item.material} ${item.customModelData}")
                    existedCustomModelData[item.material]!!.add(item.customModelData)
                }else
                    existedCustomModelData[item.material] = mutableListOf()
                setEmpireMusicDisc(itemID, item.musicDisc)
                if (item.empireGun != null) {
                    setEmpireGun(itemStack, itemID)
                    _empireGuns[itemID] = item.empireGun
                }


                _empireItems[itemID] = itemStack
                _empireEvents[itemID] = item.events
                _itemsInfo.add(
                    ItemInfo(
                        itemID,
                        empireFileConfig.getString("namespace", "empire_items") ?: "empire_items",
                        itemStack.type.name,
                        item.customModelData,
                        itemConfig.getString("texture_path")?.replace(".png", ""),
                        itemConfig.getString("model_path")?.replace(".png", "")
                    )
                )
            }

        }
    }

    private fun setEmpireMusicDisc(id: String, musicDisc: String?) {
        _empireDiscs[id] = MusicDiscs.MusicDisc().create(musicDisc) ?: return
    }

    private fun setEmpireGun(itemStack: ItemStack, id: String) {

        val meta = itemStack.itemMeta ?: return
        meta.persistentDataContainer.set(
            EmpirePlugin.empireConstants.EMPIRE_GUN_CURRENT_CLIP_SIZE,
            PersistentDataType.INTEGER,
            0
        )
        itemStack.itemMeta = meta

    }


}