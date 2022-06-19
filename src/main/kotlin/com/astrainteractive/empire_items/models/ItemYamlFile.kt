package com.astrainteractive.empire_items.models

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.addAttribute
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.models.mob.YmlMob
import com.astrainteractive.empire_items.models.recipies.CraftingTable
import com.astrainteractive.empire_items.models.recipies.Furnace
import com.astrainteractive.empire_items.models.recipies.Shapeless
import com.astrainteractive.empire_items.models.yml_item.YmlItem
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class ItemYamlFile(
    val namespace: String = "empire_items",
    val crafting_table: Map<String, CraftingTable>? = null,
    val shapeless: Map<String, Shapeless>? = null,
    val furnace: Map<String, Furnace>? = null,
    val loot: Map<String, Loot>? = null,
    val villagerTrades: Map<String, VillagerTradeInfo>? = null,
    val fontImages: Map<String, FontImage> = mapOf(),
    val merchant_recipes:Map<String,YamlMerchantRecipe>? = null,
    val yml_items: Map<String, YmlItem>? = null,
    @SerialName("sounds")
    val ymlSounds: Map<String, YmlSound> = mapOf(),
    @SerialName("mobs")
    val ymlMob: Map<String, YmlMob> = mapOf()
)
