package com.makeevrserg.empireprojekt.empirelibs.database


import com.makeevrserg.empireprojekt.EmpirePlugin
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Database for plugin
 */

class EmpireDatabase {


    /**
     * Path for your plugin database
     *
     * Should be private
     */
    private val dbPath = "${EmpirePlugin.instance.dataFolder}${File.separator}data.db"

    /**
     * Connection for your database.
     *
     * You should call this object only from DatabaseQuerries
     * @See DatabaseQuerries
     */
    companion object {
        lateinit var connection: Connection
    }

    /**
     * Function for connecting to local database
     */
    private fun connectDatabase(): Boolean {
        return try {
            connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
            true
        } catch (ex: SQLException) {
            false
        }
    }


    /**
     * Initialization for your database
     */
    private fun initDatabase() {
        if (connectDatabase())
            println(EmpirePlugin.translations.DB_SUCCESS)
        else {
            println(EmpirePlugin.translations.DB_FAIL)
        }
    }

    init {
        initDatabase()
    }

    public fun onDisable() {
        connection.close()
    }

}
