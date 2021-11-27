package com.astrainteractive.empireprojekt.empire_items.util.resource_pack.data

import com.astrainteractive.empireprojekt.empire_items.api.font.AstraFont
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
            fun fromAstraFont(a: AstraFont)=
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
