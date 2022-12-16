package com.astrainteractive.empire_itemss.api.hud

import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.async.BukkitMain
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.hud.thirst.ThirstEvent
import com.astrainteractive.empire_itemss.api.utils.IManager
import com.astrainteractive.empire_itemss.api.utils.bukkitAsyncTaskTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.di.getValue
import kotlin.collections.HashSet


class HudView(
    empireItemsApi: IReloadable<EmpireItemsAPI>,
    private val empireItemsAPI: IDependency<EmpireItemsAPI>
) : IManager {
    private val empireItemsApi by empireItemsApi
    private var actionBarWorker: BukkitTask? = null
    private var hudValueProviders = HashSet<IHudValueProvider>()
    fun addProvider(provider: IHudValueProvider) {
        hudValueProviders.add(provider)
    }

    val armorProvider = object : IHudValueProvider {
        override val id: String = "armor"
        override val position: Int = 0

        override fun provideHud(player: Player): PlayerHud {
            val font = this@HudView.empireItemsApi.fontByID["armor"]!!
            return PlayerHud(id, position, font)
        }

    }

    override fun onEnable() {
        val thirstEvent = ThirstEvent(empireItemsAPI)
//        addProvider(TestProvider("thirst_17"))
//        addProvider(TestProvider("thirst_18"))
//        addProvider(TestProvider("crisps"))
//        addProvider(TestProvider("crisps"))
//        addProvider(TestProvider("crisps"))
//        addProvider(TestProvider("crisps"))
//        addProvider(TestProvider("crisps"))
//        addProvider(TestProvider("crisps"))
//        addProvider(TestProvider("crisps"))
//        addProvider(thirstEvent.provider)
//        addProvider(armorProvider)
        actionBarWorker = bukkitAsyncTaskTimer {
            Bukkit.getOnlinePlayers().forEach { player ->
                val providers = hudValueProviders.map {
                    it.provideHud(player)
                }
                val line = HudController.build(*providers.toTypedArray())
                PluginScope.launch(Dispatchers.BukkitMain) {
                    player.sendActionBar(line)
                }
            }
        }
    }

    override fun onDisable() {
        actionBarWorker?.cancel()
        hudValueProviders.clear()
    }


}

