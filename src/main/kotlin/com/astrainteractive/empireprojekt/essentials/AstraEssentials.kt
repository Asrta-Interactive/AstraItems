package com.astrainteractive.empireprojekt.essentials

import com.astrainteractive.empireprojekt.essentials.events.EssentialsEventManager

class AstraEssentials {

    lateinit var essentialsEventManager:EssentialsEventManager

    fun onEnable(){
        essentialsEventManager = EssentialsEventManager()

    }
    fun onDisable(){
        essentialsEventManager.onDisable()

    }
}