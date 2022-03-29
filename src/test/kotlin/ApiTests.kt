import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.crafting.AstraFurnaceRecipe
import com.astrainteractive.empire_items.api.crafting.AstraShapelessRecipe
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.drop.DropApi
import com.astrainteractive.empire_items.api.utils.*
import com.astrainteractive.empire_items.empire_items.util.calcChance
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import io.kotest.core.spec.style.scopes.StringSpecRootScope
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.time.LocalDate
import kotlin.test.expect

class ApiTests : StringSpec() {
    companion object {
        lateinit var server: ServerMock
        lateinit var plugin: EmpirePlugin
    }

    @Before
    fun tearUp() {
        server = MockBukkit.mock()
        plugin = (MockBukkit.load(EmpirePlugin::class.java) as EmpirePlugin)
        Utility.sendTestFiles()
        plugin.reloadConfig()
    }


    @After
    fun tearDown() {
        MockBukkit.unmock()
    }


    @Test
    fun `Testing DropAPI`() {
        runBlocking {
            DropApi.onEnable()
        }

        "Проверка получения getDropsById"{
            assertEquals(DropApi.getDropsById("SPAWNER_1").size, 1)
        }
        "Проверка получения getDropsById"{
            assertEquals(DropApi.getDropsById("spawner_energy_1").size, 1)
        }

    }

    @Test
    fun `Testing calcChance`() {
        "Проверка calcChange"{
            assertEquals(calcChance(101), true)
            assertEquals(calcChance(0), false)
            assertEquals(calcChance(-1), false)
        }
    }

    @Test
    fun `Testing cooldown`() {
        "Проверка Cooldown"{
            val cooldown = Cooldown<String>()
            cooldown.setCooldown("test")
            assertEquals(cooldown.hasCooldown("text"), true)
            assertEquals(cooldown.hasCooldown("text", 1000L), true)
            assertEquals(cooldown.hasCooldown("text", -10L), false)
            assertEquals(cooldown.hasCooldown("text", 0L), false)
        }
    }

    @Test
    fun `Testing BukkitAstraData`() {
        "Проверка BukkitAstraData"{
            val someConstant = BukkitConstant("someKey", PersistentDataType.INTEGER)
            val itemStack = ItemStack(Material.DIAMOND)
            val itemMeta = itemStack.itemMeta!!
            assertEquals(itemMeta.getPersistentData(someConstant) == null, true)
            assertEquals(itemMeta.hasPersistentData(someConstant), false)
            itemMeta.setPersistentDataType(someConstant, 0)
            assertEquals(itemMeta.getPersistentData(someConstant), 0)
            assertEquals(itemMeta.hasPersistentData(someConstant), true)

        }
    }

    @Test
    fun `Testing BlockParser`() {
        //todo
    }

    @Test
    fun `Testing CraftingApi`() {
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
    }
}