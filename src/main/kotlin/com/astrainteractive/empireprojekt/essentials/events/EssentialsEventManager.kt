package com.astrainteractive.empireprojekt.essentials.events

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager

class EssentialsEventManager :IAstraManager {
    override val handlers: MutableList<IAstraListener> = mutableListOf()
//    val licenceAccountChecker = LicencAccoutnChecker().onEnable(this)
}