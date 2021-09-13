package com.makeevrserg.empireprojekt.empire_items.events

import com.makeevrserg.empireprojekt.empire_items.events.empireevents.*
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.BookSignEvent
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.ExperienceRepairEvent
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.ItemInteractListener
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.drop.ItemDropListener
import com.makeevrserg.empireprojekt.empire_items.events.upgrades.ItemUpgradeEvent
import com.makeevrserg.empireprojekt.empire_items.events.villagers.VillagerEvent
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.IEventManager
import com.makeevrserg.empireprojekt.empirelibs.menu.MenuListener

import com.makeevrserg.empireprojekt.essentials.music_disc.MusicDiscsNewEvent
import com.makeevrserg.empireprojekt.essentials.sit.SitEvent
import makeevrserg.empireprojekt.events.resourcepack.ProtocolLibResourcePack
import makeevrserg.empireprojekt.events.resourcepack.ResourcePackEvent
import org.bukkit.Bukkit


class GenericListener() : IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()

    init {

        BookSignEvent().onEnable(this)
//        MusicDiscsEvent().onEnable(this)
        MusicDiscsNewEvent().onEnable(this)
//        AutoBlockChangeEvent().onEnable(this)
        SitEvent().onEnable(this)
        //GunEvent().onEnable(this)
        Vampirism().onEnable(this)
        if (Bukkit.getServer().pluginManager.getPlugin("ProtocolLib") != null)
            FontProtocolLibEvent().onEnable(this)
        if (Bukkit.getServer().pluginManager.getPlugin("ProtocolLib") != null)
            ProtocolLibResourcePack().onEnable(this)
        LavaWalkerEvent().onEnable(this)
        HammerEvent().onEnable(this)
        MolotovEvent().onEnable(this)
        SoulBindEvent().onEnable(this)
        GrenadeEvent().onEnable(this)
        GrapplingHook().onEnable(this)
        ResourcePackEvent().onEnable(this)
        ExperienceRepairEvent().onEnable(this)
        MenuListener().onEnable(this)
        ItemUpgradeEvent().onEnable(this)
        ItemDropListener().onEnable(this)
        ItemInteractListener().onEnable(this)
        VillagerEvent().onEnable(this)
        PlayerShowRecipeKey().onEnable(this)

    }

}
