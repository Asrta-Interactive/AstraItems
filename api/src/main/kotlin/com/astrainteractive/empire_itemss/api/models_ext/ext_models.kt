package com.astrainteractive.empire_itemss.api.models_ext

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.calcChance
import com.atrainteractive.empire_items.models.Loot
import com.atrainteractive.empire_items.models.VillagerTradeInfo
import com.atrainteractive.empire_items.models.yml_item.Interact
import com.destroystokyo.paper.ParticleBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.utils.valueOfOrNull
import kotlin.random.Random
private val empireItemsAPI: EmpireItemsAPI
    get() = EmpireItemsAPI.instance
fun VillagerTradeInfo.VillagerTrade.toMerchantRecipe(): MerchantRecipe? {

    val result = empireItemsAPI.toAstraItemOrItemByID(id, amount) ?: return null
    val left = empireItemsAPI.toAstraItemOrItemByID(leftItem.id, leftItem.amount) ?: return null
    val right = empireItemsAPI.toAstraItemOrItemByID(middleItem?.id, middleItem?.amount ?: 1)
    return MerchantRecipe(result, 0, Int.MAX_VALUE, false).apply {
        addIngredient(left)
        right?.let { addIngredient(it) }

    }
}


fun Loot.generateItem(): ItemStack? {
    if (!calcChance(chance)) return null
    if (minAmount > maxAmount) {
        Logger.warn(
            "Wrong min: ${minAmount} and max: ${maxAmount} amounts of drop id: ${id}; dropFrom: ${dropFrom} ",
            "Loot"
        )
        return null
    }
    val amount = if (minAmount == maxAmount) minAmount else Random.nextInt(minAmount, maxAmount + 1)
    return empireItemsAPI.toAstraItemOrItemByID(id,amount)
}

fun Loot.performDrop(location: Location) {
    generateItem()?.let {
        location.world.dropItemNaturally(location, it)
    }
}

val Interact.PlayParticle.realColor: Color?
    get() = if (color != null) Color.fromRGB(Integer.decode(color?.replace("#", "0x"))) else null

val Interact.PlayParticle.particle: Particle?
    get() = valueOfOrNull<Particle>(name)

fun Interact.PlayParticle.play(location: Location) {
    ParticleBuilder(particle ?: return)
        .count(count)
        .extra(time)
        .location(location).spawn()
}

fun Interact.PlaySound.play(l: Location) {
    PluginScope.launch(Dispatchers.BukkitMain) {
        l.world.playSound(l, name, volume, pitch)
    }
}

fun Interact.PlayCommand.play(player: Player?) {
    if (asConsole)
        AstraLibs.instance.server.dispatchCommand(AstraLibs.instance.server.consoleSender, command)
    else player?.performCommand(command)
}

fun Interact.PlayPotionEffect.play(e: LivingEntity?) {
    e ?: return
    val effect = PotionEffectType.getByName(name) ?: return
    e.addPotionEffect(PotionEffect(effect, duration, amplifier, display, display, display))
}
