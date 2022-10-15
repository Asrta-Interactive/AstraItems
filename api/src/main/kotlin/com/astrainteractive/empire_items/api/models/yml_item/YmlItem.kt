package com.astrainteractive.empire_items.api.models.yml_item

import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.convertHex
import ru.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.enchants.EmpireEnchants
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.addAttribute
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType


@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
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
    val decoration: Decoration? = null,
    val book: Book? = null
) {
    val texturePath: String?
        get() = _texturePath?.replace(".png", "")
    val modelPath: String?
        get() = _modelPath?.replace(".json", "")

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Book(
        val title: String,
        val author: String,
        val pages: Map<String, List<String>>
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Decoration(
        val placeSound: String,
        val breakSound: String,
        val placeParticle: Interact.PlayParticle,
        val breakParticle: Interact.PlayParticle,
    )

    fun toItemStack(amount: Int = 1): ItemStack? {
        val itemStack = setupItemStack(amount)
        val itemMeta = itemStack?.itemMeta ?: return null

        setupItemFlags(itemMeta)
        setupEnchantments(itemMeta)
        setupAttributes(itemMeta, itemStack.type.equipmentSlot)
        setupPotion(itemMeta)
        setupCustomDurability(itemMeta)
        setupEmpireEnchants(itemMeta)
        setupLeatherArmor(itemMeta)

        setupGun(itemMeta)
        itemMeta.setDisplayName(ChatColor.WHITE.toString() + convertHex(displayName))
        itemMeta.lore = convertHex(lore)
        setupBook(itemMeta)
        itemStack.itemMeta = itemMeta
        return itemStack

    }

    private fun setupItemStack(amount: Int): ItemStack? {
        val material = Material.getMaterial(material) ?: return null
        return ItemStack(material, amount).apply {
            this.editMeta {
                it.setCustomModelData(customModelData)
                it.setPersistentDataType(BukkitConstants.ASTRA_ID, id)
            }
        }
    }

    private fun setupItemFlags(itemMeta: ItemMeta) {
        itemFlags.forEach {
            valueOfOrNull<ItemFlag>(it)?.let {
                itemMeta.addItemFlags(it)
            }
        }
    }

    private fun setupEnchantments(itemMeta: ItemMeta) {
        enchantments.forEach { (k, v) ->
            Enchantment.getByName(k)?.let {
                itemMeta.addEnchant(it, v, true)
            }
        }
    }

    private fun setupAttributes(itemMeta: ItemMeta, slot: EquipmentSlot) {
        attributes.forEach { (k, v) ->
            valueOfOrNull<Attribute>(k)?.let {
                itemMeta.addAttribute(it, v, slot)//*EquipmentSlot.values())
            }
        }
    }

    private fun setupPotion(itemMeta: ItemMeta) {
        (itemMeta as? PotionMeta)?.color = Color.WHITE
    }

    private fun setupCustomDurability(itemMeta: ItemMeta) {
        durability ?: return
        itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY, durability)
        itemMeta.setPersistentDataType(BukkitConstants.MAX_CUSTOM_DURABILITY, durability)
    }

    private fun setupLeatherArmor(itemMeta: ItemMeta) {
        val leatherArmorMeta = itemMeta as? LeatherArmorMeta ?: return
        val color = java.awt.Color.decode(armorColor)
        val r = color.red
        val g = color.green
        val b = color.blue
        leatherArmorMeta.setColor(Color.fromRGB(r, g, b))
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE)
    }

    private fun setupEmpireEnchants(itemMeta: ItemMeta) {
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
    }

    private fun setupGun(itemMeta: ItemMeta) {
        if (gun?.clipSize != null)
            itemMeta.setPersistentDataType(BukkitConstants.CLIP_SIZE, 0)
    }

    private fun setupBook(itemMeta: ItemMeta) {
        val bookMeta = itemMeta as? BookMeta ?: return
        book ?: return
        bookMeta.author = book.author
        bookMeta.title = book.title
        bookMeta.generation = BookMeta.Generation.ORIGINAL
        val pages = book.pages.values.map {
            it.joinToString("\n").HEX()
        }
        bookMeta.pages = pages
    }

}