package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.util.Files
import com.atrainteractive.empire_items.models.config.Config
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.IReloadable

object ConfigModule : IReloadable<Config>() {

    override fun initializer(): Config {
        return EmpireSerializer.toClass<Config>(Files.configFile)!!
    }
}