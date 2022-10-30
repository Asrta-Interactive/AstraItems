package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.util.Files
import com.atrainteractive.empire_items.models.config.GuiConfig
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.IReloadable

object GuiConfigModule : IReloadable<GuiConfig>() {

    override fun initializer(): GuiConfig {
        return EmpireSerializer.toClass<GuiConfig>(Files.guiConfig)!!
    }
}