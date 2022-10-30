package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.util.Translations
import ru.astrainteractive.astralibs.di.IReloadable

object TranslationModule : IReloadable<Translations>() {
    override fun initializer(): Translations {
        return Translations()
    }
}