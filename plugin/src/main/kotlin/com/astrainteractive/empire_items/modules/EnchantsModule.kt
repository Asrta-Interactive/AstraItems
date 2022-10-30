package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.util.Files
import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.IReloadable

object EnchantsModule : IReloadable<EmpireEnchantsConfig>() {

    override fun initializer(): EmpireEnchantsConfig {
        return EmpireSerializer.toClass<EmpireEnchantsConfig>(Files.enchantsModule)!!
    }
}