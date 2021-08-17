package npcs.data

import com.makeevrserg.empireprojekt.EmpirePlugin

data class NPCConfig(
    val radiusTrack: Int = 20,
    val radiusHide: Int = 70,
    val npcRemoveListTime: Long = 200,
    val spawnNPCPacketTime: Long = 500,
    val npcTrackTime: Int = 200
) {
    companion object {
        fun new(): NPCConfig {
            val section = EmpirePlugin.empireFiles.npcs.getConfig()
            val radius_track = section.getInt("radius_track", 20) ?: 20
            val radius_hide = section.getInt("radius_hide", 70) ?: 70
            val remove_list_time = section.getLong("remove_list_time", 200) ?: 200
            val spawn_packet_time = section.getLong("spawn_packet_time", 500) ?: 500
            val track_time = section.getInt("track_time", 200) ?: 200
            return NPCConfig(
                radiusTrack = radius_track,
                radiusHide = radius_hide,
                npcRemoveListTime = remove_list_time,
                spawnNPCPacketTime = spawn_packet_time,
                npcTrackTime = track_time
            )
        }
    }
}