package com.astrainteractive.empireprojekt.empire_items.api.crafting

object CraftingManager {
    private var crafting: Crafting = Crafting()

    fun load(){
        clear()
        crafting = Crafting.getCrafting().apply { createRecipes() }
    }
    fun clear(){
        crafting.clear()
    }
    fun usedInCraft(id:String): MutableSet<String> {
        val craftingTable = crafting.craftingTable.filter { it.ingredients?.values?.contains(id)==true }.map { it.result }
        val shapeless = crafting.shapeless.filter { it.input==id }.map { it.result }
        val furnace = crafting.furnace.filter { it.input==id }.map { it.result }
        val player = crafting.player.filter { it.ingredients?.values?.contains(id)==true }.map { it.result }
        val set = mutableSetOf<String>()
        set.addAll(craftingTable)
        set.addAll(shapeless)
        set.addAll(furnace)
        set.addAll(player)
        return set
    }
}