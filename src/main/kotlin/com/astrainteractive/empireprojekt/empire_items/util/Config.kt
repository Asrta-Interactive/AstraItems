package com.astrainteractive.empireprojekt.empire_items.util

import com.astrainteractive.empireprojekt.EmpirePlugin

object Config {
    //Resource pack
    var resourcePackLink:String = "https://empireprojekt.ru/files/EmpireProjektPack_Light_2.zip"
    private set
    var requestPackOnJoin:Boolean = true
        private set
    var kickOnResourcePackDeny:Boolean = false
        private set
    //Empire enchants
    var vampirismMultiplier:Double = 0.05
        private set
    //Block generation
    var generationDebug = false
        private set
    var generationDeepDebug = true
    private set
    var generateBlocks = true
        private set
    var generateOnlyOnNewChunks = false
        private set
    var maxGenerationQueueSize = 5000
        private set
    var generateBlocksGap = 2L
        private set
    var generateMaxChunksAtOnce = 5
        private set

    var tabPrefix = "%vault_prefix%"
    fun load(){
        val s = EmpirePlugin.empireFiles.configFile.getConfig()
        resourcePackLink = s.getString("resourcePack.link", resourcePackLink)!!
        requestPackOnJoin = s.getBoolean("resourcePack.requestOnJoin", requestPackOnJoin)
        kickOnResourcePackDeny = s.getBoolean("resourcePack.kickOnDeny", kickOnResourcePackDeny)

        vampirismMultiplier = s.getDouble("empireEnchants.vampirismMultiplier", vampirismMultiplier)

        generationDebug = s.getBoolean("blockGeneration.debug", generationDebug)
        generationDebug = s.getBoolean("blockGeneration.deepDebug", generationDeepDebug)
        generateBlocks = s.getBoolean("blockGeneration.generate", generateBlocks)
        generateOnlyOnNewChunks = s.getBoolean("blockGeneration.onlyOnNewChunks", generateOnlyOnNewChunks)
        maxGenerationQueueSize = s.getInt("blockGeneration.maxQueueSize", maxGenerationQueueSize)
        generateBlocksGap = s.getLong("blockGeneration.generateBlocksGap", generateBlocksGap)
        generateMaxChunksAtOnce = s.getInt("blockGeneration.maxChunksAtOnce", generateMaxChunksAtOnce)

        tabPrefix = s.getString("tabPrefix", tabPrefix)!!

    }
}