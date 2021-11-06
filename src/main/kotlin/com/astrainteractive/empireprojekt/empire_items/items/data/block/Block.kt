package com.astrainteractive.empireprojekt.items.data.block

import com.google.gson.annotations.SerializedName

data class Block(
    @SerializedName("data")
    val data:Int,
    @SerializedName("break_particles_material")
    val breakParticleMaterial:String?,
    @SerializedName("break_sound")
    val breakSound:String?,
    @SerializedName("place_sound")
    val placeSound:String?,
    @SerializedName("hardness")
    val hardness:Int?,
    @SerializedName("generate")
    val generate:Generate?
)