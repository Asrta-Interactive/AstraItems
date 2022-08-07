package com.astrainteractive.empire_items.api.utils

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.utils.HEX
import com.google.common.io.Files
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.io.File
import java.util.*


fun ItemMeta.addAttribute(attr: Attribute, amount: Double, vararg slot: EquipmentSlot?) {
    slot.forEach {
        addAttributeModifier(
            attr,
            AttributeModifier(UUID.randomUUID(), attr.name, amount, AttributeModifier.Operation.ADD_NUMBER, it)
        )
    }
}

fun ConfigurationSection.getDoubleOrNull(path:String): Double? =
     if (!this.contains(path))
        null
    else getDouble(path)


fun ItemStack.setDisplayName(name: String) {
    val meta = itemMeta
    meta?.setDisplayName(name.HEX())
    itemMeta = meta
}


private fun getFilesList() = File(
    AstraLibs.instance.dataFolder.toString() + File.separator + "items" + File.separator
).listFiles()

private fun File.isYml() = Files.getFileExtension(toString()).equals("yml", ignoreCase = true)
fun getCustomItemsFiles() = getFilesList()?.filter { it.isYml() }?.map {
    FileManager("items" + File.separator + it.name)
}
fun getCustomItemsSections(section:String) = getFilesList()?.filter { it.isYml() }?.mapNotNull {
    FileManager("items" + File.separator + it.name).getConfig().getConfigurationSection(section)
}