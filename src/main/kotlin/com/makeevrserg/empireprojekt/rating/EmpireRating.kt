package com.makeevrserg.empireprojekt.rating

import com.makeevrserg.empireprojekt.rating.commands.CommandManager
import com.makeevrserg.empireprojekt.rating.database.RatingDAO

class EmpireRating {
    companion object{

    }
    init {
        RatingDAO().createUserTable()
        CommandManager()
    }
    public fun onDisable(){

    }
}