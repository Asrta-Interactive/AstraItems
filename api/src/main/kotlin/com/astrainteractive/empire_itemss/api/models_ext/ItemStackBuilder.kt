package com.astrainteractive.empire_itemss.api.models_ext

import com.astrainteractive.empire_items.enchants.core.EmpireEnchants
import com.astrainteractive.empire_itemss.api.addAttribute
import com.astrainteractive.empire_itemss.api.emoji
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import com.atrainteractive.empire_items.models.yml_item.YmlItem
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
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.convertHex
import ru.astrainteractive.astralibs.utils.persistence.BukkitConstant
import ru.astrainteractive.astralibs.utils.persistence.Persistence.setPersistentDataType
import ru.astrainteractive.astralibs.utils.valueOfOrNull

fun YmlItem.toItemStack(amount: Int = 1) = ItemStackBuilder(this,amount).build()

class ItemStackBuilder(
    private val item: YmlItem,
    private val amount: Int = 1,
) {

    private val EMPIRE_ENCHANT = EmpireEnchants.EMPIRE_ENCHANT
    private val empireEnchantsByKey = EmpireEnchants.byKey

    fun build(): ItemStack? {

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
        itemMeta.setDisplayName(ChatColor.WHITE.toString() + convertHex(item.displayName).emoji())
        itemMeta.lore = convertHex(item.lore).map { it.emoji() }
        setupBook(itemMeta)
        itemStack.itemMeta = itemMeta
        return itemStack
    }


    private fun setupItemStack(amount: Int): ItemStack? {
        val material = Material.getMaterial(item.material) ?: return null
        return ItemStack(material, amount).apply {
            this.editMeta {
                it.setCustomModelData(item.customModelData)
                it.setPersistentDataType(BukkitConstants.ASTRA_ID, item.id)
            }
        }
    }

    private fun setupItemFlags(itemMeta: ItemMeta) {
        item.itemFlags.forEach {
            valueOfOrNull<ItemFlag>(it)?.let {
                itemMeta.addItemFlags(it)
            }
        }
    }

    private fun setupEnchantments(itemMeta: ItemMeta) {
        item.enchantments.forEach { (k, v) ->
            Enchantment.getByName(k)?.let {
                itemMeta.addEnchant(it, v, true)
            }
        }
    }

    private fun setupAttributes(itemMeta: ItemMeta, slot: EquipmentSlot) {
        item.attributes.forEach { (k, v) ->
            valueOfOrNull<Attribute>(k)?.let {
                itemMeta.addAttribute(it, v, slot)//*EquipmentSlot.values())
            }
        }
    }

    private fun setupPotion(itemMeta: ItemMeta) {
        (itemMeta as? PotionMeta)?.color = Color.WHITE
    }

    private fun setupCustomDurability(itemMeta: ItemMeta) {
        item.durability ?: return
        itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY, item.durability)
        itemMeta.setPersistentDataType(BukkitConstants.MAX_CUSTOM_DURABILITY, item.durability)
    }

    private fun setupLeatherArmor(itemMeta: ItemMeta) {
        val leatherArmorMeta = itemMeta as? LeatherArmorMeta ?: return
        val color = java.awt.Color.decode(item.armorColor)
        val r = color.red
        val g = color.green
        val b = color.blue
        leatherArmorMeta.setColor(Color.fromRGB(r, g, b))
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE)
    }

    private fun setupEmpireEnchants(itemMeta: ItemMeta) {
        item.empireEnchants.forEach { (k, v) ->
            empireEnchantsByKey[k.uppercase()]?.let {
                itemMeta.setPersistentDataType(EMPIRE_ENCHANT, 0)
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
        if (item.gun?.clipSize != null)
            itemMeta.setPersistentDataType(BukkitConstants.CLIP_SIZE, 0)
    }

    private fun setupBook(itemMeta: ItemMeta) {
        val bookMeta = itemMeta as? BookMeta ?: return
        val book = item.book ?: return
        bookMeta.author = book.author
        bookMeta.title = book.title
        bookMeta.generation = BookMeta.Generation.ORIGINAL
        val pages = book.pages.values.map {
            it.joinToString("\n").HEX()
        }
        bookMeta.pages = pages
    }
}