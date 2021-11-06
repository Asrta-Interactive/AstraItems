package com.astrainteractive.empireprojekt.empire_items.events.upgrades

import com.astrainteractive.empireprojekt.empire_items.events.upgrades.data.EmpireUpgrade

class UpgradesManager {
    val _upgradesMap: Map<String, List<EmpireUpgrade>> = EmpireUpgrade.newMap()
}