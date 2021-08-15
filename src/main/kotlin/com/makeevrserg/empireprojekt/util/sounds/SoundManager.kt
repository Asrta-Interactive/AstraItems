package com.makeevrserg.empireprojekt.util.sounds

import com.makeevrserg.empireprojekt.util.EmpireSound

class SoundManager {
    val soundsList = EmpireSound.new()
    val soundByID = EmpireSound.soundByID(soundsList)
}