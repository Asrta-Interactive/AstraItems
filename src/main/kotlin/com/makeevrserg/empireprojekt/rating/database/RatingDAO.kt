package com.makeevrserg.empireprojekt.rating.database

import com.makeevrserg.empireprojekt.empirelibs.database.EmpireDatabase
import com.makeevrserg.empireprojekt.rating.database.entities.RatingUser
import java.sql.SQLException

class RatingDAO {
    private fun connection() = EmpireDatabase.connection

    public fun createUserTable(): Boolean {
        return try {
            connection().
            prepareStatement("CREATE TABLE IF NOT EXISTS RATING_USER(minecraft_uuid varchar(36),rating int,reason varchar(256))")
                .execute()
            true
        } catch (ex: SQLException) {
            println(ex.stackTraceToString())
            return false
        }
    }
    fun getUserByUUID(uuid:String): MutableList<RatingUser> {
        return try {
            val result = connection().createStatement().executeQuery("SELECT * from RATING_USER")
            val list = mutableListOf<RatingUser>()
            while (result.next()) {
                val user = RatingUser(result.getString("minecraft_uuid"), result.getInt("rating"), result.getString("reason"))
                list.add(user)
            }
            return list
        } catch (ex: SQLException) {
            println(ex.stackTraceToString())
            mutableListOf()
        }
    }

    fun insertUserRating(uuid:String, rating:Int, reason:String): Boolean {
        return try {
            println("INSERT INTO RATING_USER (minecraft_uuid, rating, reason) VALUES (${uuid}, ${rating}, ${reason})")
            connection().prepareStatement("INSERT INTO RATING_USER VALUES (\'${uuid}\', ${rating}, \'${reason}\')").execute()
            true

        } catch (ex: SQLException) {
            println(ex.stackTraceToString())
            false
        }
    }

}