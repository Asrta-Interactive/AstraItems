package com.astrainteractive.empire_items.di

import com.astrainteractive.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.enchants.EnchantManager
import com.astrainteractive.empire_items.events.GenericListener
import com.astrainteractive.empire_items.meg.BossBarController
import com.astrainteractive.empire_items.meg.api.EmpireModelEngineAPI
import com.astrainteractive.empire_items.util.Translations
import com.astrainteractive.empire_itemss.api.crafting.CraftingApi
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.FontApi
import com.astrainteractive.empire_itemss.api.crafting.CraftingController
import com.astrainteractive.empire_itemss.api.crafting.creators.CraftingTableRecipeCreator
import com.astrainteractive.empire_itemss.api.crafting.creators.FurnaceRecipeCreator
import com.astrainteractive.empire_itemss.api.crafting.creators.ShapelessRecipeCreator
import com.astrainteractive.empire_itemss.api.items.DecorationBlockAPI
import com.atrainteractive.empire_items.models.config.Config
import com.atrainteractive.empire_items.models.config.GuiConfig
import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.*

val TranslationModule = reloadable {
    Translations()
}.alsoRemember()

val enchantsConfigModule = reloadable {
    EmpireSerializer.toClass<EmpireEnchantsConfig>(Files.enchantsModule)!!
}.alsoRemember()

val enchantMangerModule = reloadable {
    EnchantManager(enchantsConfigModule)
}.alsoRemember()

val GuiConfigModule = reloadable {
    EmpireSerializer.toClass<GuiConfig>(Files.guiConfig)!!
}.alsoRemember()

val configModule = reloadable {
    EmpireSerializer.toClass<Config>(Files.configFile)!!
}.alsoRemember()

val genericListenerModule = module {
    GenericListener()
}.alsoRemember()

val empireItemsApiModule = reloadable {
    EmpireItemsAPI()
}.alsoRemember()


val craftingTableRecipeCreatorModule = module {
    CraftingTableRecipeCreator(craftingApiModule, empireItemsApiModule)
}
val furnaceRecipeCreatorModule = module {
    FurnaceRecipeCreator(craftingApiModule, empireItemsApiModule)
}
val shapelessRecipeCreatorModule = module {
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
val craftingApiModule = reloadable {
    CraftingApi(empireItemsApiModule)
}.alsoRemember()

val empireModelEngineApiModule = reloadable {
    EmpireModelEngineAPI(empireItemsApiModule, bossBarControllerModule)
}.alsoRemember()

val bossBarControllerModule = reloadable {
    BossBarController()
}.alsoRemember()

val commandManagerModule = module {
    CommandManager()
}.alsoRemember()

val fontApiModule = module {
    FontApi(empireItemsApiModule)
}.alsoRemember()

val decorationBlockApiModule = module {
    DecorationBlockAPI(empireItemsApiModule)
}.alsoRemember()