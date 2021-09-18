package com.makeevrserg.empireprojekt.empire_items.util.resource_pack.data

data class Provider(
    val type: String?="bitmap",
    val file: String,
    val shift: List<Double>?=null,
    val size: Double?=null,
    val oversample: Int?=null,
    val chars: List<String>?,
    val height: Int?,
    val ascent: Int?
)

data class Fonts(
    val providers: List<Provider>
)