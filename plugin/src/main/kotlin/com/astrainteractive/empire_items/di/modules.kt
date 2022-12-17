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
    println("Modules: Translation created")
    Translations()
}

val enchantsConfigModule = reloadable {
    println("Modules: EmpireEnchantsConfig created")
    EmpireSerializer.toClass<EmpireEnchantsConfig>(Files.enchantsModule)!!
}

val GuiConfigModule = reloadable {
    println("Modules: GuiConfig created")
    EmpireSerializer.toClass<GuiConfig>(Files.guiConfig)!!
}

val configModule = reloadable {
    println("Modules: Config created")
    EmpireSerializer.toClass<Config>(Files.configFile)!!
}

val enchantMangerModule = reloadable {
    println("Modules: EnchantManager created")
    EnchantManager(enchantsConfigModule)
}

val genericListenerModule = reloadable {
    println("Modules: GenericListener created")
    GenericListener()
}

val empireItemsApiModule = reloadable {
    println("Modules: EmpireItemsAPI created")
    EmpireItemsAPI().also {
        Injector.forget(it)
        Injector.remember(it)
    }
}

val craftingApiModule = module {
    println("Modules: CraftingApi created")
    CraftingApi(empireItemsApiModule)
}

private val craftingTableRecipeCreatorModule = module {
    println("Modules: CraftingTableRecipeCreator created")
    CraftingTableRecipeCreator(craftingApiModule, empireItemsApiModule)
}
private val furnaceRecipeCreatorModule = module {
    println("Modules: FurnaceRecipeCreator created")
    FurnaceRecipeCreator(craftingApiModule, empireItemsApiModule)
}
private val shapelessRecipeCreatorModule = module {
    println("Modules: ShapelessRecipeCreator created")
    ShapelessRecipeCreator(craftingApiModule, empireItemsApiModule)
}
val craftingControllerModule = module {
    println("Modules: CraftingController created")
    CraftingController(
        empireItemsApiModule,
        craftingTableRecipeCreatorModule,
        furnaceRecipeCreatorModule,
        shapelessRecipeCreatorModule
    )
}

val bossBarControllerModule = module {
    println("Modules: BossBarController created")
    BossBarController()
}

val empireModelEngineApiModule = reloadable {
    println("Modules: EmpireModelEngineAPI created")
    EmpireModelEngineAPI(empireItemsApiModule, bossBarControllerModule)
}

val commandManagerModule = module {
    println("Modules: CommandManager created")
    CommandManager()
}

val fontApiModule = module {
    println("Modules: FontApi created")
    FontApi(empireItemsApiModule)
}
val empireUtilsModule = module {
    println("Modules: EmpireUtils created")
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