package com.makeevrserg.empireprojekt.events.upgrades

import com.makeevrserg.empireprojekt.events.upgrades.data.EmpireUpgrade

class UpgradesManager {
    val _upgradesMap: Map<String, List<EmpireUpgrade>> = EmpireUpgrade.newMap()
}