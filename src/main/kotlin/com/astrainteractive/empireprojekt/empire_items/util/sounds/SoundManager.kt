package com.astrainteractive.empireprojekt.empire_items.util.sounds

import com.astrainteractive.empireprojekt.empire_items.util.EmpireSound

class SoundManager {
    val soundsList = EmpireSound.new()
    val soundByID = EmpireSound.soundByID(soundsList)
}