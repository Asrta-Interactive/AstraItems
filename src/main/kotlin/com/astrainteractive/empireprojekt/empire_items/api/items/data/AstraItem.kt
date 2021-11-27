package com.astrainteractive.empireprojekt.empire_items.api.items.data

import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empireprojekt.empire_items.api.utils.addAttribute
import com.astrainteractive.empireprojekt.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.astralibs.*
import com.astrainteractive.empireprojekt.empire_items.api.items.data.interact.PlaySound
import com.astrainteractive.empireprojekt.empire_items.api.items.data.block.Block
import com.astrainteractive.empireprojekt.empire_items.api.items.data.decoration.Decoration
import com.astrainteractive.empireprojekt.empire_items.api.items.data.interact.Interact
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta


data class AstraItem(
    val namespace:String,
    val id: String,
    val displayName: String,
    val lore: List<String>?,
    val generate:Boolean,
    val material: Material,
    var texturePath: String?,
    var modelPath: String?,
    val customModelData: Int?,
    val itemFlags: List<ItemFlag>?,
    val enchantments: Map<Enchantment, Int>?,
    val empireEnchants:Map<String,Double>?,
    val durability: Int?,
    val attributes: Map<Attribute, Double>?,
    val customTags: List<String>,
    val interact: List<Interact>?,
    val musicDisc: PlaySound?,
    val block: Block?,
    val decoration: Decoration?
) {



    fun toItemStack(): ItemStack {
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta!!
        itemMeta.setDisplayName(displayName)
        itemMeta.lore = lore
        itemMeta.setCustomModelData(customModelData)
        itemMeta.setPersistentDataType(BukkitConstants.ASTRA_ID(),id)
        itemFlags?.forEach {
            itemMeta.addItemFlags(it) }
        enchantments?.forEach { (k, v) ->
            itemMeta.addEnchant(k, v, true)
        }
        attributes?.forEach { (k, v) ->
            itemMeta.addAttribute(k,v, *EquipmentSlot.values())
        }
        if (material== Material.POTION){
            (itemMeta as PotionMeta).color= Color.WHITE
        }
        empireEnchants?.forEach { (k, v) ->
            when(k.lowercase()){
                BukkitConstants.MOLOTOV.value.key->itemMeta.setPersistentDataType(BukkitConstants.MOLOTOV,v.toInt())
                BukkitConstants.GRAPPLING_HOOK.value.key->itemMeta.setPersistentDataType(BukkitConstants.GRAPPLING_HOOK,v.toInt())
                BukkitConstants.SOUL_BIND.value.key->itemMeta.setPersistentDataType(BukkitConstants.SOUL_BIND,v.toInt())
                BukkitConstants.HAMMER_ENCHANT.value.key->itemMeta.setPersistentDataType(BukkitConstants.HAMMER_ENCHANT,v.toInt())
                BukkitConstants.LAVA_WALKER_ENCHANT.value.key->itemMeta.setPersistentDataType(BukkitConstants.LAVA_WALKER_ENCHANT,v.toInt())
                BukkitConstants.VAMPIRISM_ENCHANT.value.key->itemMeta.setPersistentDataType(BukkitConstants.VAMPIRISM_ENCHANT,v.toInt())
                BukkitConstants.GRENADE_EXPLOSION_POWER.value.key->itemMeta.setPersistentDataType(BukkitConstants.GRENADE_EXPLOSION_POWER,v.toInt())
            }


        }
        itemStack.itemMeta = itemMeta
        return itemStack



    }

    companion object {


        val TAG = "AstraItem"
        private fun parseItemFlags(list: List<String>) =
            list.mapNotNull {
                valueOfOrNull<ItemFlag>(it) }

        /**
         * yml_items.<item_id>.attributes
         */
        private fun parseAttributes(s: ConfigurationSection?) =
            s?.getKeys(false)?.associate { attrName ->
                val attr = valueOfOrNull<Attribute>(attrName)
                val attrAmount = s.getDouble(attrName, 0.0)
                Pair(attr, attrAmount)
            }?.filter { it.key != null } as Map<Attribute, Double>?

        /**
         * yml_items.<item_id>.enchantements
         */
        private fun parseEnchantments(s: ConfigurationSection?) =
            s?.getKeys(false)?.associate { enchName ->
                val ench = Enchantment.getByName(enchName)
                val amount = s.getInt(enchName)
                Pair(ench, amount)
            }?.filter { it.key != null } as Map<Enchantment, Int>?

        fun ConfigurationSection.getIntOrNull(path: String): Int? =
            if (!contains(path))
                null
            else getInt(path)

        fun getItems(fileManager: FileManager): List<AstraItem>? {
            val fileConfig = fileManager.getConfig()
            val namespace = fileConfig.getString("namespace","empire_items")!!
            return fileConfig.getConfigurationSection("yml_items")?.getKeys(false)?.mapNotNull {
                getItemById(fileConfig.getConfigurationSection("yml_items.$it"),namespace)
            }
        }

        /**
         * yml_items.<item_id>
         */
        fun getItemById(section: ConfigurationSection?,namespace: String): AstraItem? {
            val id = section?.name ?: return null
            val lore = section.getHEXStringList("lore")
            val generate = section.getBoolean("generate",false)
            val displayName = section.getString("displayName")?.HEX() ?: return null
            val material = Material.getMaterial(section.getString("material") ?: return null) ?: return null
            val texturePath = section.getString("texturePath")?.replace(".png","")
            val modelPath = section.getString("modelPath")?.replace(".json","")
            val customModelData = section.getIntOrNull("customModelData")
            val itemFlags = parseItemFlags(section.getStringList("itemFlags"))
            val enchantments = parseEnchantments(section.getConfigurationSection("enchantments"))
            val durability = section.getIntOrNull("durability")
            val attributes = parseAttributes(section.getConfigurationSection("attributes"))
            val customTags = section.getStringList("customTags")
            val interact = Interact.getMultiInteract(section.getConfigurationSection("interact"))
            val musicDisc = PlaySound.getSinglePlaySound(section.getConfigurationSection("musicDisc"))
            val block = Block.getBlock(section.getConfigurationSection("block"))
            val empireEnchants = section.getConfigurationSection("empire_enchants")?.getKeys(false)?.associate { Pair(it,section.getDouble("empire_enchants.$it")) }
            return AstraItem(
                id = id,
                namespace = namespace,
                lore = lore,
                generate = generate,
                displayName = displayName,
                material = material,
                texturePath = texturePath,
                modelPath = modelPath,
                customModelData = customModelData,
                itemFlags = itemFlags,
                enchantments = enchantments,
                durability = durability,
                attributes = attributes,
                customTags = customTags,
                interact = interact,
                musicDisc = musicDisc,
                block = block,
                decoration = null,
                empireEnchants = empireEnchants
            )




        }
    }
}