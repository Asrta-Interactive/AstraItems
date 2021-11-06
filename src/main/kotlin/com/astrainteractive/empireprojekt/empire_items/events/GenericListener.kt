package com.astrainteractive.empireprojekt.empire_items.events

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager
import com.astrainteractive.astralibs.menu.MenuListener
import com.astrainteractive.empireprojekt.empire_items.events.decorations.DecorationBlockPlaceEvent
import com.astrainteractive.empireprojekt.empire_items.events.empireevents.*
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.BookSignEvent
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.ExperienceRepairEvent
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.ItemInteractListener
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop.ItemDropListener
import com.astrainteractive.empireprojekt.empire_items.events.upgrades.ItemUpgradeEvent
import com.astrainteractive.empireprojekt.empire_items.events.villagers.VillagerEvent

import com.astrainteractive.empireprojekt.essentials.music_disc.MusicDiscsNewEvent
import com.astrainteractive.empireprojekt.essentials.sit.SitEvent
import makeevrserg.empireprojekt.events.resourcepack.ProtocolLibResourcePack
import makeevrserg.empireprojekt.events.resourcepack.ResourcePackEvent
import org.bukkit.Bukkit


class GenericListener() : IAstraManager {
    override val handlers: MutableList<IAstraListener> = mutableListOf()

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

        DecorationBlockPlaceEvent().onEnable(this)

    }

}
