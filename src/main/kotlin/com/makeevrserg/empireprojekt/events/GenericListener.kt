package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.essentials.AutoBlockChangeEvent
import com.makeevrserg.empireprojekt.essentials.MusicDiscsEvent
import com.makeevrserg.empireprojekt.essentials.sit.SitEvent
import com.makeevrserg.empireprojekt.essentials.SpawnerEggBlockEvent
import com.makeevrserg.empireprojekt.events.empireevents.*
import com.makeevrserg.empireprojekt.events.empireevents.Vampirism
import com.makeevrserg.empireprojekt.events.genericevents.BookSignEvent
import com.makeevrserg.empireprojekt.events.genericevents.ExperienceRepairEvent
import com.makeevrserg.empireprojekt.events.genericevents.ItemInteractListener
import com.makeevrserg.empireprojekt.events.genericevents.drop.ItemDropListener
import com.makeevrserg.empireprojekt.events.upgrades.ItemUpgradeEvent
import com.makeevrserg.empireprojekt.events.villagers.VillagerEvent
import empirelibs.IEmpireListener
import empirelibs.IEventManager
import empirelibs.menu.MenuListener
import makeevrserg.empireprojekt.events.resourcepack.ProtocolLibResourcePack
import makeevrserg.empireprojekt.events.resourcepack.ResourcePackEvent
import org.bukkit.Bukkit


class GenericListener() : IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()

    init {
        SpawnerEggBlockEvent().onEnable(this)
        BookSignEvent().onEnable(this)
        MusicDiscsEvent().onEnable(this)
        AutoBlockChangeEvent().onEnable(this)
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
        GrenadeEvent().onEnable(this)
        ResourcePackEvent().onEnable(this)
        ExperienceRepairEvent().onEnable(this)
        MenuListener().onEnable(this)
        ItemUpgradeEvent().onEnable(this)
        ItemDropListener().onEnable(this)
        ItemInteractListener().onEnable(this)
        VillagerEvent().onEnable(this)

    }

}
