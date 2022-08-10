package com.astrainteractive.empire_items

import com.astrainteractive.empire_items.api.enchants.models._EmpireEnchantsConfig
import com.astrainteractive.empire_items.api.models._Config
import com.astrainteractive.empire_items.api.models._GuiConfig
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.empire_items.util.Translations
import com.astrainteractive.empire_items.empire_items.util._Files

object ResourceProvider {
    var translations: Translations = Translations()
        private set
    var files: _Files = _Files()
        private set
    var guiConfig: _GuiConfig = _GuiConfig.create(Files.guiConfig)
        private set
    var config: _Config = _Config.create(Files.configFile)
        private set
    var empireEnchantsConfig: _EmpireEnchantsConfig = _EmpireEnchantsConfig.create(Files.enchantsModule)
        private set

    fun reload() {
        translations = Translations()
        files = _Files()
        guiConfig = _GuiConfig.create(Files.guiConfig)
        config = _Config.create(Files.configFile)
        empireEnchantsConfig = _EmpireEnchantsConfig.create(Files.enchantsModule)
    }
}