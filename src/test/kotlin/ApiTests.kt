import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock
import com.astrainteractive.astralibs.catching
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.crafting.AstraFurnaceRecipe
import com.astrainteractive.empire_items.api.crafting.AstraShapelessRecipe
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.drop.DropApi
import com.astrainteractive.empire_items.api.utils.*
import com.astrainteractive.empire_items.empire_items.util.calcChance
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ApiTests : StringSpec({
    catching {
        runBlocking {
            server = MockBukkit.mock()
            plugin = (MockBukkit.load(EmpirePlugin::class.java) as EmpirePlugin)
            Utility.sendTestFiles()
            plugin.reloadConfig()
            DropApi.onEnable()
        }.catching { null }
    }
    "Проверка получения getDropsFrom"{
        DropApi.getDropsFrom("SPAWNER_1").size shouldBe 1
    }
    "Проверка получения getDropsById"{

        println(DropApi.dropsMap)
        println(DropApi.dropsMap.firstOrNull { it.id == "spawner_energy_1" })
        println(DropApi.getDropsById("spawner_energy_1"))
        println(DropApi.getDropsById("spawner_energy_1").size)
        assertEquals(DropApi.getDropsById("spawner_energy_1").size, 1)
    }
    "Проверка calcChange"{
        assertEquals(calcChance(101), true)
        assertEquals(calcChance(0), false)
        assertEquals(calcChance(-1), false)
        val result = whileOrMax(10) {
            return@whileOrMax calcChance(10)
        }
        assertEquals(result, true)
    }
    "Проверка Cooldown"{
        val cooldown = Cooldown<String>()
        cooldown.setCooldown("test")
        assertEquals(cooldown.hasCooldown("text"), true)
        assertEquals(cooldown.hasCooldown("text", 1000L), true)
        assertEquals(cooldown.hasCooldown("text", -10L), false)
        assertEquals(cooldown.hasCooldown("text", 0L), false)
    }
    "Проверка BukkitAstraData"{
        val someConstant = BukkitConstant("someKey", PersistentDataType.INTEGER)
        val itemStack = ItemStack(Material.DIAMOND).apply { itemMeta = ItemMetaMock() }
        val itemMeta = itemStack.itemMeta!!
        assertEquals(itemMeta.getPersistentData(someConstant) == null, true)
        assertEquals(itemMeta.hasPersistentData(someConstant), false)
        itemMeta.setPersistentDataType(someConstant, 0)
        assertEquals(itemMeta.getPersistentData(someConstant), 0)
        assertEquals(itemMeta.hasPersistentData(someConstant), true)

    }
    "Проверка CraftingAPI"{
        val furnaceReal = AstraFurnaceRecipe(
            id = "test_diamond_f",
            returns = "IRON_INGOT",
            result = "DIAMOND",
            input = "DIAMOND",
            cookTime = 600,
            exp = 10,
            amount = 1
        )
        val shapelessReal = AstraShapelessRecipe(
            id = "test_diamond_sh",
            input = "DIAMOND",
            amount = 1,
            inputs = listOf("DIAMOND"),
            result = "DIAMOND"
        )
        //todo
        assertEquals(CraftingApi.getFurnaceByInputId("test_diamond_f").first(), furnaceReal)
    }


    afterTest {
        catching {
            runBlocking {
                MockBukkit.unmock()
            }
        }
    }
}) {
    companion object {
        lateinit var server: ServerMock
        lateinit var plugin: EmpirePlugin
        var enabled: Boolean = false
        fun whileOrMax(max: Int = 1000, printIteration: Boolean = false, block: () -> Boolean): Boolean {
            var i = 0
            var result = block.invoke()
            while (!result && i < max) {
                i++
                result = block.invoke()
            }
            if (printIteration)
                println("Iteration ${i}/${max} result ${result}")
            return result
        }
    }


}

private fun <T> Unit.catching(function: () -> T?) = function.invoke()
