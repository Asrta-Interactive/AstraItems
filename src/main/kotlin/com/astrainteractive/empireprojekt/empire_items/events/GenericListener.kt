package com.astrainteractive.empireprojekt.empire_items.events

import com.astrainteractive.astraitems.events.block.BlockGenerationEvent
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager
import com.astrainteractive.astralibs.menu.MenuListener
import com.astrainteractive.empireprojekt.empire_items.events.blocks.BlockHardnessEvent
import com.astrainteractive.empireprojekt.empire_items.events.blocks.MushroomBlockBreakEvent
import com.astrainteractive.empireprojekt.empire_items.events.blocks.MushroomBlockPlaceEvent
import com.astrainteractive.empireprojekt.empire_items.events.blocks.MushroomCancelEvent
import com.astrainteractive.empireprojekt.empire_items.events.decorations.DecorationBlockPlaceEvent
import com.astrainteractive.empireprojekt.empire_items.events.empireevents.*
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.BookSignEvent
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.ExperienceRepairEvent
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.GenericEvents
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop.ItemDropListener
import com.astrainteractive.empireprojekt.empire_items.events.resourcepack.ProtocolLibResourcePack
import com.astrainteractive.empireprojekt.empire_items.events.resourcepack.ResourcePackEvent
import com.astrainteractive.empireprojekt.empire_items.events.upgrade.UpgradeEvent
import com.astrainteractive.empireprojekt.empire_items.events.villagers.VillagerEvent

import com.astrainteractive.empireprojekt.empire_items.events.empireevents.MusicDiscsNewEvent
import com.astrainteractive.empireprojekt.essentials.sit.SitEvent
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
        SlimeCatchEvent().onEnable(this)
        DeathTotemEvent().onEnable(this)
        CatKillEvent().onEnable(this)
        SoulBindEvent().onEnable(this)
        GrenadeEvent().onEnable(this)
        GrapplingHook().onEnable(this)
        VoidTotemEvent().onEnable(this)
        ResourcePackEvent().onEnable(this)
        ExperienceRepairEvent().onEnable(this)
        CoreInspectEvent().onEnable(this)
        DurabilityCraftEvent().onEnable(this)
        MenuListener().onEnable(this)
        UpgradeEvent().onEnable(this)
        ItemDropListener().onEnable(this)
        GenericEvents().onEnable(this)
        VillagerEvent().onEnable(this)
        PlayerShowRecipeKey().onEnable(this)
        BlockGenerationEvent().onEnable(this)
        BlockHardnessEvent().onEnable(this)
        MushroomBlockPlaceEvent().onEnable(this)
        MushroomBlockBreakEvent().onEnable(this)
        MushroomCancelEvent().onEnable(this)

        DecorationBlockPlaceEvent().onEnable(this)

    }

}

