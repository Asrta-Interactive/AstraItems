package com.astrainteractive.empire_items.gui

import com.atrainteractive.empire_items.models.config.Config
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.menu.AstraMenuSize
import ru.astrainteractive.astralibs.menu.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.IPlayerHolder
import ru.astrainteractive.astralibs.menu.Menu
import ru.astrainteractive.astralibs.utils.HEX

class ResourcePack(val player: Player,val config:Config) : Menu() {
    override var menuTitle: String = "Принять ресурс-пак?"
    override val menuSize: AstraMenuSize
        get() = AstraMenuSize.XXS
    override val playerMenuUtility: IPlayerHolder = DefaultPlayerHolder(player)
    private val backButtonIndex: Int = 8
    private val backButton = ItemStack(Material.COMPASS).apply {
        editMeta {
            it.setDisplayName("#ca471bНазад".HEX())
            it.lore = buildList {
                add("#ca1b1bТы вообще кто?".HEX())
                add("#ca1b1bДумаешь я буду это читать?".HEX())
                add("#ca1b1bКакой ресурс-пак".HEX())
                add("#ca1b1bНе буду я ничего принимать кроме ислама".HEX())
            }
        }
    }
    private val acceptButtonIndex: Int = 3
    private val acceptButton = ItemStack(Material.DIAMOND).apply {
        editMeta {
            it.setDisplayName("#DEBA2DПринять".HEX())
            it.lore = buildList {
                add("#1B76CAОн необходим чтобы вы могли видеть:".HEX())
                add("#1B76CAНовые предметы".HEX())
                add("#1B76CAНовый UI".HEX())
                add("#1B76CAНовые эмодзи".HEX())
                add("#1B76CAНовых мобов".HEX())
                add("#1B76CAИ многое другое".HEX())
            }
        }
    }
    private val denyButtonIndex: Int = 5
    private val denyButton = ItemStack(Material.BARRIER).apply {
        editMeta {
            it.setDisplayName("#ca471bОтклонить".HEX())
            it.lore = buildList {
                add("#ca1b1bБез него ваш экспириенс будет неполным:".HEX())
                add("#ca1b1bВы не увидите новые предметы".HEX())
                add("#ca1b1bВы не увидите новый UI".HEX())
                add("#ca1b1bНе сможете использовать эмодзи".HEX())
                add("#ca1b1bВместо мобов будет лошадиная броня".HEX())
                add("#ca1b1bВместо многих предметов будут палки".HEX())
            }
        }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        when (e.slot) {
            acceptButtonIndex -> {
                playerMenuUtility.player.closeInventory()
                player.setResourcePack(config.resourcePack.link)
                Logger.log("Игрок ${player.name} согласился скачать ресурс-пак", "ResourcePackMenu")
            }

            backButtonIndex, denyButtonIndex -> {
                playerMenuUtility.player.closeInventory()
                Logger.log("Игрок ${player.name} отказался скачать ресурс-пак", "ResourcePackMenu")
            }
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) = Unit

    override fun onCreated() {
        inventory.setItem(acceptButtonIndex, acceptButton)
        inventory.setItem(denyButtonIndex, denyButton)
        inventory.setItem(backButtonIndex, backButton)
    }
}