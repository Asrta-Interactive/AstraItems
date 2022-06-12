package com.astrainteractive.empire_items.models.yml_item

import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.addAttribute
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType

fun a(){
Attribute.GENERIC_MAX_HEALTH
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class YmlItem(
    val id: String,
    val displayName: String,
    val lore: List<String> = listOf(),
    val material: String,
    @SerialName("texturePath")
    val _texturePath: String? = null,
    @SerialName("modelPath")
    val _modelPath: String? = null,
    val customModelData: Int = 0,
    val itemFlags: List<String> = listOf(),
    val namespace: String = "empire_items",
    @SerialName("empire_enchants")
    val empireEnchants: Map<String, String> = mapOf(),
    val enchantments: Map<String, Int> = mapOf(),
    val durability: Int? = null,
    val armorColor: String? = null,
    val attributes: Map<String, Double> = mapOf(),
    val customTags: List<String> = listOf(),
    val block: Block? = null,
    val musicDisc: Interact.PlaySound? = null,
    val interact: Map<String, Interact> = mapOf(),
    val gun: Gun? = null,
    val decoration: Decoration? = null
) {
    val texturePath: String?
        get() = _texturePath?.replace(".png", "")
    val modelPath: String?
        get() = _modelPath?.replace(".json", "")

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Decoration(
        val placeSound: String,
        val breakSound: String,
        val placeParticle: Interact.PlayParticle,
        val breakParticle: Interact.PlayParticle,
    )

    fun toItemStack(amount: Int = 1): ItemStack? {
        val itemStack = ItemStack(Material.getMaterial(material) ?: return null, amount)
        val itemMeta = itemStack.itemMeta!!
        itemMeta.setCustomModelData(customModelData)
        itemMeta.setPersistentDataType(BukkitConstants.ASTRA_ID, id)

        itemFlags.forEach {
            valueOfOrNull<ItemFlag>(it)?.let {
                itemMeta.addItemFlags(it)
            }
        }
        enchantments.forEach { (k, v) ->
            Enchantment.getByName(k)?.let {
                itemMeta.addEnchant(it, v, true)
            }
        }
        attributes.forEach { (k, v) ->
            valueOfOrNull<Attribute>(k)?.let {
                itemMeta.addAttribute(it, v, itemStack.type.equipmentSlot)//*EquipmentSlot.values())
            }
        }
        if (material == Material.POTION.name) {
            (itemMeta as PotionMeta).color = Color.WHITE
        }
        if (durability != null) {
            itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY, durability)
            itemMeta.setPersistentDataType(BukkitConstants.MAX_CUSTOM_DURABILITY, durability)
        }
        (itemMeta as? LeatherArmorMeta?)?.let {
            val color = java.awt.Color.decode(armorColor)
            val r = color.red
            val g = color.green
            val b = color.blue
            (itemMeta as LeatherArmorMeta).setColor(Color.fromRGB(r, g, b))
            itemMeta.addItemFlags(ItemFlag.HIDE_DYE)
        }
        empireEnchants.forEach { (k, v) ->
            EmpireEnchants.byKey[k.uppercase()]?.let {
                itemMeta.setPersistentDataType(EmpireEnchants.EMPIRE_ENCHANT, 0)
                itemMeta.setPersistentDataType(it, v.toIntOrNull() ?: return@let)
            }

            v.toIntOrNull()?.let {
                val bukkitConstant = BukkitConstant(k, PersistentDataType.INTEGER)
                itemMeta.setPersistentDataType(bukkitConstant, it)
            }

            when (k.lowercase()) {
                BukkitConstants.MOLOTOV.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.MOLOTOV,
                    v.toIntOrNull() ?: 1
                )
                BukkitConstants.GRAPPLING_HOOK.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.GRAPPLING_HOOK,
                    v
                )
                BukkitConstants.SOUL_BIND.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.SOUL_BIND,
                    v.toIntOrNull() ?: 0
                )
                BukkitConstants.HAMMER_ENCHANT.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.HAMMER_ENCHANT,
                    v.toIntOrNull() ?: 0
                )
                BukkitConstants.GRENADE_EXPLOSION_POWER.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.GRENADE_EXPLOSION_POWER,
                    v.toInt() ?: 1
                )
                BukkitConstants.SLIME_CATCHER.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.SLIME_CATCHER,
                    v
                )
                BukkitConstants.CORE_INSPECT.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.CORE_INSPECT,
                    v.toIntOrNull() ?: 5
                )
                BukkitConstants.VOID_TOTEM.value.key -> itemMeta.setPersistentDataType(BukkitConstants.VOID_TOTEM, v)
                BukkitConstants.TOTEM_OF_DEATH.value.key -> itemMeta.setPersistentDataType(
                    BukkitConstants.TOTEM_OF_DEATH,
                    v
                )
                BukkitConstants.CRAFT_DURABILITY.value.key -> {
                    itemMeta.setPersistentDataType(BukkitConstants.CRAFT_DURABILITY, v.toIntOrNull() ?: 1)
                    itemMeta.setPersistentDataType(BukkitConstants.MAX_CUSTOM_DURABILITY, v.toIntOrNull() ?: 1)
                    itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY, v.toIntOrNull() ?: 1)
                }
            }


        }
        gun?.let {
            if (it.clipSize != null)
                itemMeta.setPersistentDataType(BukkitConstants.CLIP_SIZE, 0)
        }
        itemMeta.setDisplayName(ChatColor.WHITE.toString() + convertHex(displayName))
        itemMeta.lore = convertHex(lore)
        itemStack.itemMeta = itemMeta
        return itemStack


    }

}