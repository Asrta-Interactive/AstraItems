package com.astrainteractive.empire_itemss.api.crafting.creators

interface IRecipeCreator<T> {
    fun build(recipe: T)
}