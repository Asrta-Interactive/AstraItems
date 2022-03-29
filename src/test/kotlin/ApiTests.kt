import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.drop.DropApi
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
import java.time.LocalDate
class ApiTests: StringSpec() {
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
            assertEquals(calcChance(101),true)
            assertEquals(calcChance(0),false)
            assertEquals(calcChance(-1),false)
        }
    }
}