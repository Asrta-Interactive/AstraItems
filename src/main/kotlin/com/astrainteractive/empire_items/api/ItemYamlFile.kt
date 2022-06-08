package com.astrainteractive.empire_items.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.addAttribute
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.empire_items.empire_items.util.calcChance
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
import kotlin.math.min
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
    val yml_items: Map<String, YmlItem>? = null,
    val ymlSounds: Map<String, YmlSound>? = null
)

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class YmlSound(
    val id: String,
    val sounds: List<String>,
    val namespace: String = "empire_items"
)

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class FontImage(
    val id: String,
    val path: String,
    val height: Int = 12,
    val ascent: Int = 12,
    val data: Int,
    val blockSend: Boolean = false
) {
    companion object {
        private const val count: Int = 0x3400
    }

    val char: String
        get() = (count + data).toChar().toString()
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class CraftingTable(
    val id: String,
    val result: String = id,
    val amount: Int = 1,
    val pattern: List<String>,
    val ingredients: Map<Char, String>
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val shapedRecipe = ShapedRecipe(namespaceKey, result.toAstraItemOrItem(amount) ?: return)
        if (pattern.size == 3)
            shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
        else if (pattern.size == 2)
            shapedRecipe.shape(pattern[0], pattern[1])
        ingredients.forEach { (ch, item) ->
            val itemStack = item.toAstraItemOrItem() ?: return@forEach
            val choice: RecipeChoice = CraftingApi.getRecipeChoice(itemStack)
            if (ch.equals('x', ignoreCase = true))
                shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
            else
                shapedRecipe.setIngredient(ch, choice)
        }
        CraftingApi.addRecipe(id, result, shapedRecipe)
    }
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Shapeless(
    val id: String,
    val result: String = id,
    val amount: Int = 1,
    val input: String? = null,
    val inputs: List<String> = listOf()
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val resultItem = result.toAstraItemOrItem(amount) ?: return
        val inputItem = input.toAstraItemOrItem()
        val shapelessRecipe = ShapelessRecipe(namespaceKey, resultItem)
        inputItem?.let { CraftingApi.getRecipeChoice(it) }?.let { shapelessRecipe.addIngredient(it) }
        inputs.forEach { it ->
            val rc = CraftingApi.getRecipeChoice(it.toAstraItemOrItem() ?: return@forEach)
            shapelessRecipe.addIngredient(rc)
        }
        CraftingApi.addRecipe(id, result, shapelessRecipe)
    }
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Furnace(
    val id: String,
    val result: String = id,
    val returns: String? = null,
    val amount: Int = 1,
    val input: String,
    @SerialName("cook_time")
    val cookTime: Int = 20,
    val exp: Int = 20
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val resultItem = result.toAstraItemOrItem(amount) ?: return
        val inputItem = input.toAstraItemOrItem() ?: return
        val recipeChoice = CraftingApi.getRecipeChoice(inputItem)
        val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
            namespaceKey,
            resultItem,
            recipeChoice,
            exp.toFloat(),
            cookTime
        )
        CraftingApi.addRecipe(id, result, furnaceRecipe)
    }
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Loot(
    val id: String,
    val dropFrom: String,
    val minAmount: Int = 1,
    val maxAmount: Int = 2,
    val chance: Double = 0.2
) {
    fun generateItem(): ItemStack? {
        if (!calcChance(chance)) return null
        if (minAmount > maxAmount) {
            Logger.warn("Wrong min: ${minAmount} and max: ${maxAmount} amounts of drop id: ${id}; dropFrom: ${dropFrom} ", "Loot")
            return null
        }
        val amount = if (minAmount==maxAmount) minAmount else Random.nextInt(minAmount, maxAmount)
        return id.toAstraItemOrItem(amount)
    }

    fun performDrop(location: Location) {
        generateItem()?.let {
            location.world.dropItemNaturally(location, it)
        }

    }
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class VillagerTradeInfo(
    val id: String,
    val profession: String,
    val trades: Map<String, VillagerTrade>
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class VillagerTrade(
        val id: String,
        val chance: Double = 100.0,
        val amount: Int = 1,
        val minUses: Int = 10,
        val maxUses: Int = 20,
        val minLevel: Int = 1,
        val maxLevel: Int = 5,
        val leftItem: VillagerTradeItem,
        val middleItem: VillagerTradeItem

    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class VillagerTradeItem(
            val id: String,
            val minAmount: Int = 1,
            val maxAmount: Int = 5,
            val amount: Int,
        )
    }
}

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class YmlItem(
    val id: String,
    val displayName: String,
    val lore: List<String> = listOf(),
    val material: String,
    val texturePath: String? = null,
    val modelPath: String? = null,
    val customModelData: Int = 0,
    val itemFlags: List<String> = listOf(),
    val namespace: String? = null,
    @SerialName("empire_enchants")
    val empireEnchants: Map<String, String> = mapOf(),
    val enchantments: Map<String, Int> = mapOf(),
    val durability: Int? = null,
    val armorColor: String? = null,
    val attributes: Map<String, Double> = mapOf(),
    val customTags: List<String> = listOf(),
    val block: Block? = null,
    val musicDisc: PlaySound? = null,
    val interact: Map<String, Interact> = mapOf(),
    val gun: Gun? = null,
) {
    fun toItemStack(amount: Int = 1): ItemStack? {
        val itemStack = ItemStack(Material.getMaterial(material) ?: return null, amount)
        val itemMeta = itemStack.itemMeta!!
        itemMeta.setCustomModelData(customModelData)
        itemMeta.setPersistentDataType(BukkitConstants.ASTRA_ID, id)

        itemFlags?.forEach {
            valueOfOrNull<ItemFlag>(it)?.let {
                itemMeta.addItemFlags(it)
            }
        }
        enchantments?.forEach { (k, v) ->
            Enchantment.getByName(k)?.let {
                itemMeta.addEnchant(it, v, true)
            }
        }
        attributes?.forEach { (k, v) ->
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
        empireEnchants?.forEach { (k, v) ->
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

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Gun(
        val cooldown: Int? = null,
        val recoil: Double? = null,
        val clipSize: Int? = null,
        val bulletWeight: Double = 1.0,
        val bulletTrace: Int = 100,
        val color: String? = null,
        val damage: Double? = null,
        val reload: String? = null,
        val particle: String? = null,
        val noAmmoSound: String = "",
        val reloadSound: String = "",
        val fullSound: String = "",
        val shootSound: String = "",
        val radius: Double = 1.0,
        val radiusSneak: Double = 4.0,
        val explosion: Int? = null,
        val advanced: Advanced? = null
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Advanced(
            val armorPenetration: Map<String, Double> = mapOf(),
            val onHit: OnHit? = null
        ) {
            @Suppress("PROVIDED_RUNTIME_TOO_LOW")
            @Serializable
            data class OnHit(
                val fireTicks: Int? = null,
                val ignite: Int? = null,
                val playPotionEffect: Map<String, Interact.PlayPotionEffect> = mapOf()
            )
        }

    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Block(
        val breakParticle: String = "",
        val breakSound: String = "",
        val placeSound: String = "",
        val data: Int,
        val hardness: Int? = null,
        val ignoreCheck: Boolean = false,
        val generate: Generate? = null,
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Generate(
            val generateInChunkChance: Int,
            val minPerChunk: Int,
            val maxPerChunk: Int,
            val minPerDeposit: Int,
            val maxPerDeposit: Int,
            val minY: Int,
            val maxY: Int,
            val replaceBlocks: Map<String, Int> = mapOf(),
            val world: String? = null,
        )
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlaySound(
        val name: String,
        val pitch: Float = 1f,
        val volume: Float = 1f
    ) {
        fun play(l: Location) {
            l.world.playSound(l, name, volume, pitch)
        }
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Interact(
        val eventList: List<String>,
        val cooldown: Int? = null,
        val playParticle: Map<String, PlayParticle> = mapOf(),
        val playSound: Map<String, PlaySound> = mapOf(),
        val playCommand: Map<String, PlayCommand> = mapOf(),
        val playPotionEffect: Map<String, PlayPotionEffect> = mapOf(),
        val removePotionEffect: List<String> = listOf()
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class PlayParticle(
            val name: String,
            val count: Int = 20,
            val time: Double = 0.1
        )

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class PlayCommand(
            val command: String,
            val asConsole: Boolean = false
        )

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class PlayPotionEffect(
            val name: String,
            val amplifier: Int = 1,
            val duration: Int = 200,
            val display: Boolean = true
        ) {
            fun play(e: LivingEntity?) {
                e ?: return
                val effect = PotionEffectType.getByName(name) ?: return
                e.addPotionEffect(PotionEffect(effect, duration, amplifier, display, display, display))
            }
        }
    }
}