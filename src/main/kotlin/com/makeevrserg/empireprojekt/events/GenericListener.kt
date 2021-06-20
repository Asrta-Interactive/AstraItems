package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import com.makeevrserg.empireprojekt.events.blocks.EmpireBlocks
import com.makeevrserg.empireprojekt.events.empireevents.*
import com.makeevrserg.empireprojekt.events.genericlisteners.ExperienceRepairEvent
import com.makeevrserg.empireprojekt.events.genericlisteners.ItemDropListener
import com.makeevrserg.empireprojekt.events.genericlisteners.ItemInteractListener
import com.makeevrserg.empireprojekt.events.sit.SitEvent
import com.makeevrserg.empireprojekt.menumanager.MenuListener


class GenericListener {


    private var _itemInteractListener: ItemInteractListener = ItemInteractListener()
    var _itemDropListener: ItemDropListener = ItemDropListener()
    var _itemUpgradeEvent: ItemUpgradeEvent = ItemUpgradeEvent()
    private var _menuListener: MenuListener = MenuListener()
    private var _experienceRepairEvent: ExperienceRepairEvent = ExperienceRepairEvent()
    private var _resourcePackEvent: ResourcePackEvent = ResourcePackEvent()
    private var _grenadeEvend: Grenade = Grenade()
    private var _molotov: Molotov = Molotov()
    private var _hammer: Hammer = Hammer()
    private var _lavaWalker: LavaWalker = LavaWalker()
    private var _protocolLibHandler: ProtocolLibHandler = ProtocolLibHandler()
    private var _vampirismEnchant: Vampirism = Vampirism()
    private var _gun: Gun = Gun()
    private var _empireBlocks = EmpireBlocks()
    private var _sitEvent: SitEvent = SitEvent()
    var _craftEvent = CraftEvent()
    private var _empireMusicDiscs = MusicDiscs()
    //private var _empireMobs = EmpireMobs()
    fun onDisable() {
        _itemInteractListener.onDisable()
        _itemDropListener.onDisable()
        _itemUpgradeEvent.onDisable()
        _menuListener.onDisable()
        _experienceRepairEvent.onDisable()
        _resourcePackEvent.onDisable()
        _grenadeEvend.onDisable()
        _hammer.onDisable()
        plugin.server.pluginManager.getPlugin("protocollib")?.let {
            _protocolLibHandler.onDisable()
        }
        _lavaWalker.onDisable()
        _vampirismEnchant.onDisable()
        _molotov.onDisable()
        _gun.onDisable()
        _empireBlocks.onDisable()
        _craftEvent.onDisable()
        _empireMusicDiscs.onDisable()
        _sitEvent.onDisable()
        //_empireMobs.onDisable()
    }
}