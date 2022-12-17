package com.astrainteractive.empire_items.di

import com.astrainteractive.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.enchants.EnchantManager
import com.astrainteractive.empire_items.events.GenericListener
import com.astrainteractive.empire_items.meg.BossBarController
import com.astrainteractive.empire_items.meg.EmpireModelEngineAPI
import com.astrainteractive.empire_items.util.Translations
import com.astrainteractive.empire_itemss.api.crafting.CraftingApi
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.FontApi
import com.astrainteractive.empire_itemss.api.crafting.CraftingController
import com.astrainteractive.empire_itemss.api.crafting.creators.CraftingTableRecipeCreator
import com.astrainteractive.empire_itemss.api.crafting.creators.FurnaceRecipeCreator
import com.astrainteractive.empire_itemss.api.crafting.creators.ShapelessRecipeCreator
import com.astrainteractive.empire_itemss.api.items.DecorationBlockAPI
import com.astrainteractive.empire_itemss.api.utils.EmpireUtils
import com.atrainteractive.empire_items.models.config.Config
import com.atrainteractive.empire_items.models.config.GuiConfig
import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.*

val TranslationModule = reloadable {
    Translations()
}

val enchantsConfigModule = reloadable {
    EmpireSerializer.toClass<EmpireEnchantsConfig>(Files.enchantsModule)!!
}

val GuiConfigModule = reloadable {
    EmpireSerializer.toClass<GuiConfig>(Files.guiConfig)!!
}

val configModule = reloadable {
    EmpireSerializer.toClass<Config>(Files.configFile)!!
}

val enchantMangerModule = reloadable {
    EnchantManager(enchantsConfigModule)
}

val genericListenerModule = reloadable {
    GenericListener()
}

val empireItemsApiModule = reloadable {
    EmpireItemsAPI().also {
        Injector.forget(it)
        Injector.remember(it)
    }
}

val craftingApiModule = module {
    CraftingApi(empireItemsApiModule)
}

private val craftingTableRecipeCreatorModule = module {
    CraftingTableRecipeCreator(craftingApiModule, empireItemsApiModule)
}
private val furnaceRecipeCreatorModule = module {
    FurnaceRecipeCreator(craftingApiModule, empireItemsApiModule)
}
private val shapelessRecipeCreatorModule = module {
    ShapelessRecipeCreator(craftingApiModule, empireItemsApiModule)
}
val craftingControllerModule = module {
    CraftingController(
        empireItemsApiModule,
        craftingTableRecipeCreatorModule,
        furnaceRecipeCreatorModule,
        shapelessRecipeCreatorModule
    )
}

val bossBarControllerModule = module {
    BossBarController()
}

val empireModelEngineApiModule = reloadable {
    EmpireModelEngineAPI(empireItemsApiModule, bossBarControllerModule)
}

val commandManagerModule = module {
    CommandManager()
}

val fontApiModule = module {
    FontApi(empireItemsApiModule)
}
val empireUtilsModule = module {
    EmpireUtils(
        empireItemsApiModule,
        fontApiModule
    ).also {
        Injector.forget(it)
        Injector.remember(it)
    }
}
//val decorationBlockApiModule = module {
//    DecorationBlockAPI(empireItemsApiModule)
//}.alsoRemember()