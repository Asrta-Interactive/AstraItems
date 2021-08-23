//package com.makeevrserg.empireprojekt.citizens_npc
//
//import com.makeevrserg.empireprojekt.npc.data.EmpireNPC
//import net.citizensnpcs.api.CitizensAPI
//import org.bukkit.entity.EntityType
//
//class CitizenNPC(npc:EmpireNPC) {
//
//    init {
//        val citizen = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,npc.id)
//        citizen.spawn(npc.location)
//    }
//}