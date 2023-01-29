package com.astrainteractive.empire_items.api.crafting.creators.core

interface RecipeCreator<T> {
    fun build(recipe: T)
}