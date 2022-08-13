package com.astrainteractive.empire_items.empire_items.events


import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.MenuListener
import com.astrainteractive.empire_items.api.utils.getPlugin
import com.astrainteractive.empire_items.empire_items.events.blocks.*
import com.astrainteractive.empire_items.empire_items.events.api_events.DecorationEvent
import com.astrainteractive.empire_items.empire_items.events.api_events.FontProtocolLibEvent
import com.astrainteractive.empire_items.empire_items.events.api_events.ModelEngineEvent
import com.astrainteractive.empire_items.empire_items.events.api_events.NewModelEngineEvent
import com.astrainteractive.empire_items.empire_items.events.empireevents.*
import com.astrainteractive.empire_items.empire_items.events.resourcepack.ProtocolLibResourcePackEvent
import com.astrainteractive.empire_items.empire_items.events.resourcepack.ResourcePackEvent
import com.astrainteractive.empire_items.empire_items.events.genericevents.VillagerEvent

import com.astrainteractive.empire_items.empire_items.events.empireevents.MusicDiscsEvent
import com.astrainteractive.empire_items.empire_items.events.genericevents.*
import org.bukkit.Bukkit


class GenericListener : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()


    val blocksEventModule = {
        BlockGenerationEvent()
        BlockHardnessEvent()
        MushroomBlockBreakEvent()
        MushroomBlockPlaceEvent()
        MushroomCancelEvent()
//        TestMushroomEvent()
    }
    val empireEvents = {
        LavaWalkerEvent()
        HammerEvent()
        MolotovEvent()
        MusicDiscsEvent().onEnable(this)
        SlimeCatchEvent()
        DeathTotemEvent()
        SoulBindEvent()
        GrenadeEvent()
        GrapplingHook()
        VoidTotemEvent()
        GunEvent()
        getPlugin("CoreProtect")?.let {
            CoreInspectEvent()
        }
    }
    val genericEvents = {
        MenuListener().onEnable(this)
        ExperienceRepairEvent()
        ItemDropEvent()
        ItemInteractEvent()
        VillagerEvent()
        PlayerShowRecipeKeyEvent()
        ResourcePackEvent()
        BookSignEvent().onEnable(this)

    }
    val apiEvents = {
        getPlugin("ProtocolLib")?.let {
            FontProtocolLibEvent().onEnable(this)
            ProtocolLibResourcePackEvent().onEnable(this)
        }
        getPlugin("ModelEngine")?.let {
//            ModelEngineEvent().onEnable(this)
            NewModelEngineEvent()
        }
        DecorationEvent()
    }

    init {
        blocksEventModule()
        empireEvents()
        genericEvents()
        apiEvents()
    }
}

