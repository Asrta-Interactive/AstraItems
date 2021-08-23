//package com.makeevrserg.empireprojekt.citizens_npc
//
//import com.makeevrserg.empireprojekt.EmpirePlugin
//import com.makeevrserg.empireprojekt.npc.NPCManager
//import com.makeevrserg.empireprojekt.npc.data.EmpireNPC
//import com.makeevrserg.empireprojekt.npc.data.NPCConfig
//import com.makeevrserg.empireprojekt.empirelibs.FileManager
//
//class CitizensManager {
//    companion object{
//
//        lateinit var fileManager: FileManager
//        lateinit var npcConfig: NPCConfig
//        lateinit var npcs: List<EmpireNPC>
//    }
//
//    private fun onEnable() {
//        NPCManager.fileManager = FileManager("config/npcs.yml")
//        NPCManager.npcConfig = NPCConfig.new()!!
//
//        val npcList = mutableListOf<EmpireNPC>()
//
//
//        for (npcID in NPCManager.fileManager.getConfig().getConfigurationSection("npcs")?.getKeys(false) ?: listOf()) {
//            val npc = EmpireNPC.new(npcID) ?: continue
//            npcList.add(npc)
//        }
//
//        NPCManager.empireNPCList = npcList
//
//
//
//
//    }
//
//    init {
//        if (EmpirePlugin.instance.server.pluginManager.getPlugin("ProtocolLib") != null)
//            onEnable()
//    }
//}