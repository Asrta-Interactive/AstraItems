package com.makeevrserg.empireprojekt.items

import com.makeevrserg.empireprojekt.events.empireevents.Gun
import com.makeevrserg.empireprojekt.ESSENTIALS.MusicDiscs
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.genericlisteners.EmpireCommandEvent
import com.makeevrserg.empireprojekt.events.genericlisteners.EmpireEvent
import com.makeevrserg.empireprojekt.events.genericlisteners.EmpireParticleEvent
import com.makeevrserg.empireprojekt.events.genericlisteners.EmpireSoundEvent
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.files.FileManager
import org.bukkit.*
import org.bukkit.inventory.meta.PotionMeta
import java.util.*

class EmpireItems {
    private var _empireItems: MutableMap<String, ItemStack> = mutableMapOf()
    private var _empireBlocks:MutableMap<String,ItemStack> = mutableMapOf()
    private var _itemsInfo: MutableList<ItemInfo> = mutableListOf()
    private var _empireEvents: MutableMap<String, List<EmpireEvent>> = mutableMapOf()

    val empireItems: MutableMap<String, ItemStack>
        get() = _empireItems
    val empireEvents: MutableMap<String, List<EmpireEvent>>
        get() = _empireEvents
    private val _empireGuns: MutableMap<String, Gun.EmpireGun> = mutableMapOf()
    val empireGuns: MutableMap<String, Gun.EmpireGun>
        get() = _empireGuns

    private val _empireDiscs: MutableMap<String, MusicDiscs.MusicDisc> = mutableMapOf()
    val empireDiscs: MutableMap<String, MusicDiscs.MusicDisc>
        get() = _empireDiscs

    val itemsInfo: MutableList<ItemInfo>
        get() = _itemsInfo

    private var existedCustomModelData: MutableMap<String, MutableList<Int>?>? = mutableMapOf()

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
            val empireFileConfig = empireFile.getConfig() ?: continue//file.yml
            if (!empireFileConfig.contains("yml_items"))
                continue
            for (itemID: String in empireFileConfig.getConfigurationSection("yml_items")!!.getKeys(false)) {
                val itemConfig: ConfigurationSection =
                    empireFileConfig.getConfigurationSection("yml_items")!!.getConfigurationSection(itemID)!!

                val material: Material? = Material.getMaterial(itemConfig.getString("material")!!)
                material ?: continue
                val itemStack = ItemStack(material)
                var itemMeta: ItemMeta = itemStack.itemMeta!!
                itemMeta.setDisplayName(EmpireUtils.HEXPattern(itemConfig.getString("display_name") ?: continue))
                itemMeta.lore = EmpireUtils.HEXPattern(itemConfig.getStringList("lore"))
                val customModelData: Int = itemConfig.getInt("custom_model_data")

                if (existedCustomModelData != null)
                    if (existedCustomModelData!!.contains(material.name)) {
                        if (existedCustomModelData!![material.name]!!.contains(customModelData)) {
                            println(ChatColor.RED.toString() + "Used CustomModelData ${material.name} data=${customModelData} item=${itemID}")
                        }
                        existedCustomModelData!![material.name]!!.add(customModelData)
                    } else {
                        existedCustomModelData!![material.name] = mutableListOf()
                        existedCustomModelData!![material.name]!!.add(customModelData)
                    }
                itemMeta.setCustomModelData(customModelData)

                if (itemConfig.contains("durability")) {
                    itemMeta.persistentDataContainer.set(
                        EmpirePlugin.empireConstants.EMPIRE_DURABILITY,
                        PersistentDataType.INTEGER,
                        itemConfig.getInt("durability")
                    )
                    itemMeta.persistentDataContainer.set(
                        EmpirePlugin.empireConstants.MAX_CUSTOM_DURABILITY,
                        PersistentDataType.INTEGER,
                        itemConfig.getInt("durability")
                    )
                }

                if (itemConfig.contains("item_flags"))
                    setItemFlags(itemConfig.getStringList("item_flags"), itemMeta)

                if (itemConfig.contains("enchantements"))
                    setEnchantements(itemConfig.getConfigurationSection("enchantements")!!, itemMeta)

                if (itemConfig.contains("attributes"))
                    setAttributes(itemConfig.getConfigurationSection("attributes")!!, itemMeta)

                if (itemConfig.contains("interact"))
                    setEvents(itemID, itemConfig.getConfigurationSection("interact")!!)

                if (itemConfig.contains("empire_enchants"))
                    setEmpireEnchant(itemMeta, itemConfig.getConfigurationSection("empire_enchants")!!)

                if (itemConfig.contains("empire_gun"))
                    setEmpireGun(itemMeta, itemID, itemConfig.getConfigurationSection("empire_gun")!!)
                if (itemConfig.contains("music_disc"))
                    setEmpireMusicDisc(itemID, itemConfig.getConfigurationSection("music_disc")!!)
                itemMeta.persistentDataContainer.set(
                    EmpirePlugin.empireConstants.empireID,
                    PersistentDataType.STRING,
                    itemID
                )
                if (itemStack.type == Material.POTION) {
                    val potionMeta = itemMeta as PotionMeta
                    potionMeta.color = Color.WHITE
                    itemMeta = potionMeta
                }
                itemStack.itemMeta = itemMeta
                if (itemConfig.contains("block"))
                    _empireBlocks[itemID] = itemStack

                _empireItems[itemID] = itemStack
                _itemsInfo.add(
                    ItemInfo(
                        itemID,
                        empireFileConfig.getString("namespace", "empire_items") ?: "empire_items",
                        material.name,
                        customModelData,
                        itemConfig.getString("texture_path")?.replace(".png", ""),
                        itemConfig.getString("model_path")?.replace(".png", "")
                    )
                )
            }

        }
        existedCustomModelData = null
    }

    private fun setEmpireMusicDisc(id: String, discSect: ConfigurationSection) {
        _empireDiscs[id] = MusicDiscs.MusicDisc().init(discSect) ?: return
    }

    private fun setEmpireGun(itemMeta: ItemMeta, id: String, gunSect: ConfigurationSection) {
        _empireGuns[id] = Gun.EmpireGun().init(gunSect) ?: return
        itemMeta.persistentDataContainer.set(
            EmpirePlugin.empireConstants.EMPIRE_GUN_CURRENT_CLIP_SIZE,
            PersistentDataType.INTEGER,
            0
        )

    }


    private fun setEmpireEnchant(itemMeta: ItemMeta, enchantSection: ConfigurationSection) {
        val enchantsMap = EmpirePlugin.empireConstants.getEnchantsMap()
        for (enchant in enchantSection.getKeys(false)) {
            itemMeta.persistentDataContainer.set(
                enchantsMap[enchant] ?: continue,
                PersistentDataType.DOUBLE,
                enchantSection.getDouble(enchant)
            )
        }
    }

    private fun setEvents(item: String, section: ConfigurationSection) {
        fun getPotionEffects(potionEffectsSection: ConfigurationSection?): MutableList<PotionEffect> {
            potionEffectsSection ?: return mutableListOf()
            val potionEffects: MutableList<PotionEffect> = mutableListOf()
            for (effect in potionEffectsSection.getKeys(false)) {
                val effectSect: ConfigurationSection =
                    potionEffectsSection.getConfigurationSection(effect)!!
                potionEffects.add(
                    PotionEffect(
                        PotionEffectType.getByName(effect) ?: continue,
                        effectSect.getInt("duration", 0),
                        effectSect.getInt("amplifier", 0)
                    )
                )
            }
            return potionEffects

        }

        fun getPotionsRemove(poitionEffect: List<String>?): MutableList<PotionEffectType> {
            poitionEffect ?: return mutableListOf()
            val potionEffectRemove: MutableList<PotionEffectType> = mutableListOf()
            for (effect in poitionEffect)
                potionEffectRemove.add(PotionEffectType.getByName(effect) ?: continue)
            return potionEffectRemove

        }

        fun getSoundsEvent(soundsSection: ConfigurationSection?): MutableList<EmpireSoundEvent> {
            soundsSection ?: return mutableListOf()
            val soundsEvent: MutableList<EmpireSoundEvent> = mutableListOf()
            soundsEvent.add(
                EmpireSoundEvent(
                    soundsSection.getString("name") ?: return soundsEvent,
                    soundsSection.getDouble("volume", 1.0),
                    soundsSection.getDouble("pitch", 1.0)
                )
            )
            return soundsEvent

        }

        fun getParticleEvent(particleSection: ConfigurationSection?): MutableList<EmpireParticleEvent> {
            particleSection ?: return mutableListOf()
            val particleEvent: MutableList<EmpireParticleEvent> = mutableListOf()
            particleEvent.add(
                EmpireParticleEvent(
                    particleSection.getString("name") ?: return particleEvent,
                    particleSection.getInt("count", 1),
                    particleSection.getDouble("time", 1.0)
                )
            )
            return particleEvent
        }

        fun getCommands(commandsSection: ConfigurationSection?): MutableList<EmpireCommandEvent> {

            commandsSection ?: return mutableListOf()
            val commandEvent: MutableList<EmpireCommandEvent> = mutableListOf()
            for (i in commandsSection.getKeys(false)) {
                val sect: ConfigurationSection = commandsSection.getConfigurationSection(i)!!
                commandEvent.add(
                    EmpireCommandEvent(
                        sect.getString("command") ?: continue,
                        sect.getBoolean("as_console", false)
                    )
                )
            }
            return commandEvent
        }

        val empireEventList: MutableList<EmpireEvent> = mutableListOf()
        for (eventInteractName in section.getKeys(false)) {
            val eventSection: ConfigurationSection = section.getConfigurationSection(eventInteractName)!!


            empireEventList.add(
                EmpireEvent.Builder()
                    .eventName(eventSection.getStringList("events_names"))
                    .potionEffectsAdd(getPotionEffects(eventSection.getConfigurationSection("potion_effect")))
                    .potionEffectRemove(getPotionsRemove(eventSection.getStringList("remove_effect")))
                    .soundsPlay(getSoundsEvent(eventSection.getConfigurationSection("play_sound")))
                    .particlesPlay(getParticleEvent(eventSection.getConfigurationSection("play_particle")))
                    .commandsPlay(getCommands(eventSection.getConfigurationSection("play_command")))
                    .cooldown((eventSection.getDouble("cooldown") * 20).toInt())
                    .build()
            )

        }
        _empireEvents[item] = empireEventList
    }

    private fun setItemFlags(itemFlags: List<String>, itemMeta: ItemMeta) {
        for (flag in itemFlags) {
            val itemFlag: ItemFlag = ItemFlag.valueOf(flag)
            itemMeta.addItemFlags(itemFlag)
        }
    }

    private fun setEnchantements(enchantementsSection: ConfigurationSection, itemMeta: ItemMeta) {
        for (enchantementName in enchantementsSection.getKeys(false)) {
            val enchantment: Enchantment =
                Enchantment.getByKey(NamespacedKey.minecraft(enchantementName.lowercase())) ?: continue
            itemMeta.addEnchant(
                enchantment,
                enchantementsSection.getInt(enchantementName),
                true
            )
        }
    }

    private fun setAttributes(attributes: ConfigurationSection, itemMeta: ItemMeta) {
        for (key in attributes.getKeys(false)) {
            val attributeSection: ConfigurationSection = attributes.getConfigurationSection(key)!!
            val attribute: Attribute = Attribute.valueOf(attributeSection.getString("name") ?: continue)
            val slot: EquipmentSlot = EquipmentSlot.valueOf(attributeSection.getString("equipment_slot") ?: continue)
            itemMeta.addAttributeModifier(
                attribute,
                AttributeModifier(
                    UUID.randomUUID(),
                    attribute.name,
                    attributeSection.getDouble("amount"),
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot
                )
            )
        }
    }


}