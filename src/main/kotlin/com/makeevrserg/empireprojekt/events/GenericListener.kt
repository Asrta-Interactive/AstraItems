package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.ESSENTIALS.AutoBlockChangeEvent
import com.makeevrserg.empireprojekt.ESSENTIALS.MusicDiscsEvent
import com.makeevrserg.empireprojekt.events.empireevents.*
import com.makeevrserg.empireprojekt.events.genericevents.ExperienceRepairEvent
import com.makeevrserg.empireprojekt.events.genericevents.ItemDropListener
import com.makeevrserg.empireprojekt.events.genericevents.ItemInteractListener
import com.makeevrserg.empireprojekt.ESSENTIALS.sit.SitEvent
import com.makeevrserg.empireprojekt.events.blocks.events.MushroomBlockEventHandler
import com.makeevrserg.empireprojekt.events.enchants.Vampirism
import com.makeevrserg.empireprojekt.menumanager.MenuListener



//Mananger for all of events
class GenericListener {


    private var _itemInteractListener: ItemInteractListener = ItemInteractListener()
    var _itemDropListener: ItemDropListener = ItemDropListener()
    var _itemUpgradeEvent: ItemUpgradeEvent = ItemUpgradeEvent()
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
    val mushroomBlockEventHandler = MushroomBlockEventHandler()
    private var _empireMusicDiscs = MusicDiscsEvent()
    //private var _loreBooks = LoreBooks()
    //private var _empireMobs = EmpireMobs()
    private var empireFixEvent = EmpireItemFixEvent()
    fun onDisable() {
        _itemInteractListener.onDisable()
        _itemDropListener.onDisable()
        _itemUpgradeEvent.onDisable()
        _menuListener.onDisable()
        _experienceRepairEvent.onDisable()
        _resourcePackEvent.onDisable()
        _grenadeEventEvend.onDisable()
        _hammerEvent.onDisable()
        instance.server.pluginManager.getPlugin("protocollib")?.let {
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
        //_loreBooks.onDisable()
        //_empireMobs.onDisable()
        //_empireBlock.onDisable()
    }
}
