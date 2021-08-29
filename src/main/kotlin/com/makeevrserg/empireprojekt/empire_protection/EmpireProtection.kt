package com.makeevrserg.empireprojekt.empire_protection

import com.makeevrserg.empireprojekt.empirelibs.FileManager

class EmpireProtection {
    companion object{
        lateinit var configFm:FileManager
        lateinit var regionsFm:FileManager
    }
    init {
        configFm = FileManager("EmpireProtection/config.yml")
        regionsFm = FileManager("EmpireProtection/regions.yml")
    }
}