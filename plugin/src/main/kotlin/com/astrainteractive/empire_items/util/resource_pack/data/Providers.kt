package com.astrainteractive.empire_items.util.resource_pack.data

import com.atrainteractive.empire_items.models.FontImage
import org.apache.commons.lang.StringEscapeUtils

data class Providers(
    var providers:MutableList<Provider>
){
    data class Provider(
        val type:String?=null,
        val file:String?=null,
        val shift:DoubleArray?=null,
        val size:Double?=null,
        val oversample:Int?=null,
        val chars:List<String>?=null,
        val height:Int?=null,
        val ascent:Int?=null
    ){
        companion object{
            fun fromAstraFont(a: FontImage)=
                Provider(
                    type = "bitmap",
                    file=a.path,
                    chars = listOf(StringEscapeUtils.escapeJava(a.char)),
                    height = a.height,
                    ascent = a.ascent
                )


        }
    }
}
