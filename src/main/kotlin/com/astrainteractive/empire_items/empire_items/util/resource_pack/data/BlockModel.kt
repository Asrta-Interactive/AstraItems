package com.astrainteractive.empire_items.empire_items.util.resource_pack.data

import com.google.gson.annotations.SerializedName

data class BlockModel(
    val multipart:MutableList<Multipart> = mutableListOf()
)
data class Multipart(
    @SerializedName("when")
    val _when:Map<String,Boolean>,
    val apply: Apply
)
data class Apply(val model:String)