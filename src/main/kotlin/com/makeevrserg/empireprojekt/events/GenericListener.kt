package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.essentials.AutoBlockChangeEvent
import com.makeevrserg.empireprojekt.essentials.MusicDiscsEvent
import com.makeevrserg.empireprojekt.essentials.sit.SitEvent
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.events.MushroomBlockEventHandler
import com.makeevrserg.empireprojekt.events.empireevents.*
import com.makeevrserg.empireprojekt.events.empireevents.Vampirism
import com.makeevrserg.empireprojekt.events.genericevents.BookSignEvent
import com.makeevrserg.empireprojekt.events.genericevents.ExperienceRepairEvent
import com.makeevrserg.empireprojekt.events.genericevents.ItemInteractListener
import com.makeevrserg.empireprojekt.events.genericevents.drop.ItemDropListener
import com.makeevrserg.empireprojekt.events.upgrades.ItemUpgradeEvent
import com.makeevrserg.empireprojekt.events.villagers.VillagerEvent
import empirelibs.menu.MenuListener


//Mananger for all of events
class GenericListener {

    private var _itemInteractListener: ItemInteractListener = ItemInteractListener()
    private var _itemDropListener: ItemDropListener = ItemDropListener()
    private var _itemUpgradeEvent: ItemUpgradeEvent = ItemUpgradeEvent()
    private var _menuListener: MenuListener = MenuListener()
    private var _experienceRepairEvent: ExperienceRepairEvent = ExperienceRepairEvent()
    private var _resourcePackEvent: ResourcePackEvent = ResourcePackEvent()
    private var _grenadeEventEvend: GrenadeEvent = GrenadeEvent()
    private var _molotovEvent: MolotovEvent = MolotovEvent()
    private var _hammerEvent: HammerEvent = HammerEvent()
    private var _lavaWalkerEvent: LavaWalkerEvent = LavaWalkerEvent()
    private var _Font_protocolLibEvent: FontProtocolLibEvent = FontProtocolLibEvent()
    private var _vampirismEnchant: Vampirism = Vampirism()
    private var _gunEvent: GunEvent = GunEvent()
    private var _sitEvent: SitEvent = SitEvent()
    private var _autoBlockChange = AutoBlockChangeEvent()
    private val mushroomBlockEventHandler = MushroomBlockEventHandler()
    private var _empireMusicDiscs = MusicDiscsEvent()
    private var _villagerEvent = VillagerEvent()
    private var empireFixEvent = EmpireItemFixEvent()
    private var bookSignEvent = BookSignEvent()

    fun onDisable() {
        _itemInteractListener.onDisable()
        _itemDropListener.onDisable()
        _itemUpgradeEvent.onDisable()
        _menuListener.onDisable()
        _experienceRepairEvent.onDisable()
        _resourcePackEvent.onDisable()
        _grenadeEventEvend.onDisable()
        _hammerEvent.onDisable()
        EmpirePlugin.instance.server.pluginManager.getPlugin("protocollib")?.let {
            _Font_protocolLibEvent.onDisable()
        }
        _lavaWalkerEvent.onDisable()
        _vampirismEnchant.onDisable()
        _molotovEvent.onDisable()
        _gunEvent.onDisable()
        _empireMusicDiscs.onDisable()
        _sitEvent.onDisable()
        _autoBlockChange.onDisable()
        empireFixEvent.onDisable()
        mushroomBlockEventHandler.onDisable()
        _villagerEvent.onDisable()
        bookSignEvent.onDisable()
    }
}
