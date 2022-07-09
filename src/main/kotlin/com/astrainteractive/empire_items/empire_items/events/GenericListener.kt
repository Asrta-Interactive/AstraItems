package com.astrainteractive.empire_items.empire_items.events


import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.MenuListener
import com.astrainteractive.empire_items.empire_items.events.blocks.*
import com.astrainteractive.empire_items.empire_items.events.decoration.DecorationEvent
import com.astrainteractive.empire_items.empire_items.events.empireevents.*
import com.astrainteractive.empire_items.empire_items.events.genericevents.BookSignEvent
import com.astrainteractive.empire_items.empire_items.events.genericevents.ExperienceRepairEvent
import com.astrainteractive.empire_items.empire_items.events.genericevents.ItemInteractEvent
import com.astrainteractive.empire_items.empire_items.events.genericevents.ItemDropEvent
import com.astrainteractive.empire_items.empire_items.events.resourcepack.ProtocolLibResourcePackEvent
import com.astrainteractive.empire_items.empire_items.events.resourcepack.ResourcePackEvent
import com.astrainteractive.empire_items.empire_items.events.villagers.VillagerEvent

import com.astrainteractive.empire_items.empire_items.events.empireevents.MusicDiscsEvent
import org.bukkit.Bukkit


class GenericListener : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()


    init {
        if (Bukkit.getServer().pluginManager.getPlugin("ProtocolLib") != null) {
            FontProtocolLibEvent().onEnable(this)
            ProtocolLibResourcePackEvent().onEnable(this)
        }
        if (Bukkit.getServer().pluginManager.getPlugin("CoreProtect") != null)
            CoreInspectEvent()
        BookSignEvent().onEnable(this)
        if (Bukkit.getServer().pluginManager.getPlugin("ModelEngine") != null)
            ModelEngineEvent().onEnable(this)
        MusicDiscsEvent().onEnable(this)
        LavaWalkerEvent()
        HammerEvent()
        MolotovEvent()
        SlimeCatchEvent()
        DeathTotemEvent()
        CatKillEvent()
        SoulBindEvent()
        GrenadeEvent()
        GrapplingHook()
        VoidTotemEvent()
        ResourcePackEvent()
        ExperienceRepairEvent()
        DurabilityCraftEvent()
        GunEvent()
        MenuListener().onEnable(this)
//        UpgradeEvent()
        ItemDropEvent()
        ItemInteractEvent()
        VillagerEvent()
        PlayerShowRecipeKeyEvent()
        BlockGenerationEvent()
        BlockHardnessEvent()
        MushroomBlockPlaceEvent()
        MushroomBlockBreakEvent()
        MushroomCancelEvent()
        TestMushroomEvent()
        DecorationEvent()


    }

}

