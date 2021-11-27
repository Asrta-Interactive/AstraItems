package com.astrainteractive.empireprojekt.empire_items.gui

import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.Menu
import com.astrainteractive.astralibs.menu.PaginatedMenu
import org.bukkit.entity.Player


class PlayerMenuUtility(override var player: Player): AstraPlayerMenuUtility(player){
    var categoriesPage = 0
    var categoryId:String? = null
    var categoryPage = 0
    var craftingPage = 0
    val prevItems:MutableList<String> = mutableListOf()
}
abstract class AstraMenu: Menu()

abstract class AstraPaginatedMenu:PaginatedMenu()