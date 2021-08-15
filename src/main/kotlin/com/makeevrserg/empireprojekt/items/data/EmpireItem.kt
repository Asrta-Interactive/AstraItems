package com.makeevrserg.empireprojekt.items.data

import com.google.gson.annotations.SerializedName
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.items.data.block.Block
import com.makeevrserg.empireprojekt.items.data.interact.Interact
import com.makeevrserg.empireprojekt.items.data.interact.Sound
import empirelibs.EmpireUtils
import empirelibs.EmpireUtils.Companion.valueOfOrNull
import empirelibs.EmpireYamlParser
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*


data class EmpireItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("namespace")
    val namespace:String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("material")
    val material: String,
    @SerializedName("texture_path")
    var texturePath: String?,
    @SerializedName("model_path")
    var modelPath: String?,
    @SerializedName("custom_model_data")
    val customModelData: Int,
    @SerializedName("item_flags")
    val itemFlags: List<String>?,
    @SerializedName("enchantements")
    val enchantments: Map<String, Int>?,
    @SerializedName("durability")
    val durability: Int?,
    @SerializedName("attributes")
    val attributes: Map<String, Double>?,
    @SerializedName("lore")
    val lore: List<String>?,
    @SerializedName("empire_enchants")
    val empireEnchants: Map<String, Double>?,
    @SerializedName("interact")
    val interact: List<Interact>?,
    @SerializedName("music_disc")
    val musicDisc: Sound?,
    @SerializedName("block")
    val block: Block?
) {



    public fun valueOfOrNull(value: String): ItemFlag? {
        return try {
            ItemFlag.valueOf(value)
        } catch (e: IllegalArgumentException) {
            null
        }

    }

    private fun addItemFlags(itemMeta: ItemMeta) {
        for (flag in itemFlags ?: return) {
            itemMeta.addItemFlags(valueOfOrNull(flag) ?: continue)
        }
    }

    private fun addItemEnchantments(itemMeta: ItemMeta) {
        for ((ench, level) in enchantments ?: return)
            itemMeta.addEnchant(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(ench)) ?: continue, level, true)
    }

    private fun setPotionMeta(itemMeta: ItemMeta) {
        if (itemMeta is PotionMeta)
            itemMeta.color = Color.WHITE
    }

    private fun setEmpireID(itemMeta: ItemMeta) {
        itemMeta.persistentDataContainer.set(EmpirePlugin.empireConstants.empireID, PersistentDataType.STRING, id)
    }

    private fun setEmpireDurability(itemMeta: ItemMeta) {
        durability ?: return
        if (durability == 0)
            return
        itemMeta.persistentDataContainer.set(
            EmpirePlugin.empireConstants.EMPIRE_DURABILITY,
            PersistentDataType.INTEGER,
            durability
        )
        itemMeta.persistentDataContainer.set(
            EmpirePlugin.empireConstants.MAX_CUSTOM_DURABILITY,
            PersistentDataType.INTEGER,
            durability
        )
    }

    private fun addEmpireEnchant(itemMeta: ItemMeta) {
        empireEnchants ?: return
        val map = EmpirePlugin.empireConstants.getEnchantsMap()
        for ((ench, value) in empireEnchants)
            itemMeta.persistentDataContainer.set(map[ench] ?: continue, PersistentDataType.DOUBLE, value)

    }


    private fun setItemAttributes(itemMeta: ItemMeta) {
        for ((attr, amount) in attributes ?: return) {
            val attribute = EmpireUtils.valueOfOrNull<Attribute>(attr) ?: continue
            itemMeta.addAttributeModifier(
                attribute,
                AttributeModifier(
                    UUID.randomUUID(),
                    attribute.name,
                    amount,
                    AttributeModifier.Operation.ADD_NUMBER,
                    Material.getMaterial(material)!!.equipmentSlot

                )
            )
        }
    }

    private fun Any?.checkForNull(str:String):Boolean{
        if (this==null) {
            println("Вы не ввели $str в предмете $id")
            return true
        }
        return false
    }
    public fun getItemStack(): ItemStack? {
        this.texturePath = this.texturePath?.replace(".png","")
        this.modelPath = this.modelPath?.replace(".json","")
        if (id.checkForNull("id"))
            return null
        if (namespace.checkForNull("namespace"))
            return null
        if (displayName.checkForNull("displayName"))
            return null
        if (material.checkForNull("material"))
            return null
        if (id.checkForNull("id"))
            return null


        val material = Material.getMaterial(material) ?: return null
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta ?: return null
        itemMeta.setDisplayName(EmpireUtils.HEXPattern(displayName))
        itemMeta.lore = EmpireUtils.HEXPattern(lore)
        itemMeta.setCustomModelData(customModelData)

        addItemFlags(itemMeta)
        addItemEnchantments(itemMeta)
        setPotionMeta(itemMeta)
        setItemAttributes(itemMeta)
        addEmpireEnchant(itemMeta)
        setEmpireDurability(itemMeta)
        setEmpireID(itemMeta)
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}