package makeevrserg.empireprojekt.random_items.data

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.AstraYamlParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.BetterConstants
import com.astrainteractive.empireprojekt.empire_items.util.EmpireUtils
import com.astrainteractive.empireprojekt.empire_items.util.valueOfOrNull
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.random.Random

data class RandomItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("custom_model_datas")
    val customModelDatas: List<Int>?,
    @SerializedName("material")
    val material: String,
    @SerializedName("item_flags")
    val itemFlags: List<String>?,
    @SerializedName("durability")
    val durability: MinMax?,

    @SerializedName("display_names")
    val displayNames: List<String>,
    @SerializedName("lores")
    val lores: List<List<String>>?,

    @SerializedName("attributes")
    val attributes: List<NamedMinMax>?,
    @SerializedName("enchantements")
    val enchantements: List<NamedMinMax>?
) {

    private fun getDisplayName(): String {
        if (displayNames.isEmpty())
            return ""

        return AstraUtils.HEXPattern(displayNames[Random.nextInt(displayNames.size)])
    }

    private fun getLore(): List<String> {
        lores ?: return mutableListOf()
        if (lores?.isEmpty())
            return mutableListOf()
        val lore = AstraUtils.HEXPattern(lores[Random.nextInt(lores.size)])


        return EmpireUtils.emojiPattern(lore)
    }

    private fun getCustomModelData(): Int {
        customModelDatas ?: return 0
        if (customModelDatas.isEmpty())
            return 0
        return customModelDatas[Random.nextInt(customModelDatas.size)]
    }

    private fun getDurability(): Int {
        durability ?: return 0
        return Random.nextInt(durability.amountMin, durability.amountMax)
    }

    private fun setEmpireDurability(itemMeta: ItemMeta) {
        val durability = getDurability()
        if (durability == 0)
            return
        itemMeta.persistentDataContainer.set(
            BetterConstants.EMPIRE_DURABILITY.value,
            PersistentDataType.INTEGER,
            durability
        )
        itemMeta.persistentDataContainer.set(
            BetterConstants.MAX_CUSTOM_DURABILITY.value,
            PersistentDataType.INTEGER,
            durability
        )
    }

    private fun setAttributes(itemMeta: ItemMeta) {
        for (attr in attributes ?: return) {
            if (attr.chance < Random.nextDouble(100.0))
                continue

            val attribute = EmpireUtils.valueOfOrNull<Attribute>(attr.name) ?: continue
            itemMeta.addAttributeModifier(
                attribute,
                AttributeModifier(
                    UUID.randomUUID(),
                    attribute.name,
                    Random.nextDouble(attr.amountMin, attr.amountMax),
                    AttributeModifier.Operation.ADD_NUMBER,
                    Material.getMaterial(material)!!.equipmentSlot

                )
            )
        }

    }

    private fun addItemFlags(itemMeta: ItemMeta) {
        for (flag in itemFlags ?: return) {
            itemMeta.addItemFlags(valueOfOrNull(flag) ?: continue)
        }
    }

    private fun addItemEnchantments(itemMeta: ItemMeta) {
        for (ench in enchantements ?: return) {
            if (ench.chance < Random.nextDouble(100.0))
                continue
            itemMeta.addEnchant(
                EnchantmentWrapper.getByKey(NamespacedKey.minecraft(ench.name)) ?: continue,
                Random.nextInt(ench.amountMin.toInt(), ench.amountMax.toInt()),
                true
            )
        }
    }

    public fun build(): ItemStack? {
        val mMaterial = Material.getMaterial(material) ?: return null
        val itemStack = ItemStack(mMaterial)
        val itemMeta = itemStack.itemMeta ?: return null
        itemMeta.setDisplayName(getDisplayName())
        itemMeta.lore = getLore()
        itemMeta.setCustomModelData(getCustomModelData())
        setEmpireDurability(itemMeta)
        setAttributes(itemMeta)
        addItemEnchantments(itemMeta)
        addItemFlags(itemMeta)
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    companion object {
        fun new() =
            AstraYamlParser.fromYAML<List<RandomItem>>(
                EmpirePlugin.empireFiles.randomItems.getConfig(),
                object : TypeToken<List<RandomItem?>?>() {}.type,
                listOf("random_items")
            )

    }
}
