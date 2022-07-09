package com.astrainteractive.empire_items.modules.boss_fight

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.setDisplayName
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.empire_items.util.then
import com.astrainteractive.empire_items.models.GUI_CONFIG
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack


class PlayersInviteMenu(override val playerMenuUtility: AstraPlayerMenuUtility) : PaginatedMenu() {
    override val backButtonIndex: Int = 13
    override val backPageButton: ItemStack = GUI_CONFIG.settings.buttons.backButton.toAstraItemOrItem()!!

    override val nextButtonIndex: Int = 17
    override val nextPageButton: ItemStack = GUI_CONFIG.settings.buttons.nextButton.toAstraItemOrItem()!!

    val readyButtonIndex: Int = 14
    val readyPageButton: ItemStack = ItemStack(Material.DIAMOND).apply {
        setDisplayName("#03b1fcНачать бой".HEX())
        lore = convertHex(
            listOf(
                "&7При нажатии на эту кнопку",
                "&7Вас и принявших ваше приглашение игроков",
                "&7телепортирует на арену"
            )
        )
    }
    val pendingPageButton: ItemStack = ItemStack(Material.DIAMOND).apply {
        setDisplayName("#fc1c03Начать бой".HEX())
        lore = convertHex(listOf("&7Нельзя начать бой, &7пока есть игроки, которые", "&7не приняли приглашение"))
    }

    override val prevButtonIndex: Int = 9
    override val prevPageButton: ItemStack = GUI_CONFIG.settings.buttons.prevButton.toAstraItemOrItem()!!

    override val maxItemsAmount: Int
        get() = viewModel.players.value.size
    override var menuName: String = "Выберите союзников".HEX()
    override val menuSize: AstraMenuSize = AstraMenuSize.XS
    override val maxItemsPerPage: Int = 9
    override var page: Int = 0
    private val viewModel = PlayersInviteViewModel(playerMenuUtility)
    val items = IntRange(0, 64).mapIndexed { i, _ ->
        ItemStack(Material.DIAMOND).apply {
            setDisplayName(i.toString())
        }
    }

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        if (e.slot == backButtonIndex)
            inventory.close()
        else if (IntRange(0, maxItemsPerPage).contains(e.slot))
            viewModel.onPlayerClicked(e.slot)
        else if (e.slot == readyButtonIndex) {
            if (viewModel.ready) {
                viewModel.onReadyClicked()
                inventory.close()
            }
        }

    }

    override fun setMenuItems() {
        addManageButtons()
        if (viewModel.ready)
            inventory.setItem(readyButtonIndex, readyPageButton)
        else
            inventory.setItem(readyButtonIndex, pendingPageButton)

        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            val item = viewModel.players.value.elementAtOrNull(index) ?: continue
            val itemStack = item.skull.clone().apply {
                setDisplayName("${item.accepted.then("&l#fcba03") ?: item.invited.then("#fc1c03") ?: "#FFFFFF"}${item.player.name}".HEX())
                lore = if (!item.invited)
                    listOf(
                        "&7Чтобы пригласить игрока нажмите на иконку".HEX()
                    )
                else
                    listOf(
                        "&7Игрок приглашён".HEX(),
                        "&7Принял приглашение: ${item.accepted.then("#fcba03Да") ?: "#fc1c03Нет"}".HEX()
                    )
            }
            inventory.setItem(i, itemStack)
        }
    }

    var job: Job = AsyncHelper.launch {
        viewModel.players.collectLatest {
            AsyncHelper.callSyncMethod {
                setMenuItems()
            }
        }
    }

    val manager = object : EventManager {
        override val handlers: MutableList<EventListener> = mutableListOf()

    }
    val inventoryCloseEvent = DSLEvent.event(InventoryCloseEvent::class.java, manager) {
        if (it.inventory.holder is PlayersInviteMenu) {
            viewModel.onDestroy()
            job.cancel()
            manager.onDisable()
        }
    }
}