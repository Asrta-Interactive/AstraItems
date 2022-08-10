import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock
import com.astrainteractive.astralibs.utils.catching
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.utils.*
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
//            Utility.sendTestFiles()
            plugin.reloadConfig()
        }.catching { null }
    }
    "Проверка Cooldown" {
        val cooldown = Cooldown<String>()
        cooldown.setCooldown("test")
        assertEquals(cooldown.hasCooldown("text"), true)
        assertEquals(cooldown.hasCooldown("text", 1000L), true)
        assertEquals(cooldown.hasCooldown("text", -10L), false)
        assertEquals(cooldown.hasCooldown("text", 0L), false)
    }
    "Проверка BukkitAstraData" {
        val someConstant = BukkitConstant("someKey", PersistentDataType.INTEGER)
        val itemStack = ItemStack(Material.DIAMOND).apply { itemMeta = ItemMetaMock() }
        val itemMeta = itemStack.itemMeta!!
        assertEquals(itemMeta.getPersistentData(someConstant) == null, true)
        assertEquals(itemMeta.hasPersistentData(someConstant), false)
        itemMeta.setPersistentDataType(someConstant, 0)
        assertEquals(itemMeta.getPersistentData(someConstant), 0)
        assertEquals(itemMeta.hasPersistentData(someConstant), true)

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
    }


}

private fun <T> Unit.catching(function: () -> T?) = function.invoke()
