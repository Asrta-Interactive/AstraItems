package com.astrainteractive.empireprojekt.empire_items.util.resource_pack.data

import com.google.gson.annotations.SerializedName

data class Model(
    var parent:String,
    var textures: Textures?,
    var overrides:MutableList<Override>?=null,
    var gui_light:Any?=null,
    var display:Any?=null
){
    companion object{
        fun getPotionModel() = Model(
            parent = "item/generated",
            textures = Textures("item/potion_overlay","item/potion"),
            overrides = mutableListOf()
        )
    }

}
data class Textures(
    var layer0:String?=null,
    val layer1:String?=null,
    val particle:String?=layer0,
    val all:String?=layer0
)
data class Override(
    val model:String,
    val predicate: Predicate
)
data class Predicate(
    @SerializedName("custom_model_data")
    val customModelData:Int?=null,
    val blocking:Int?=null,
    val pulling:Int?=null,
    val pull:Double?=null
)
