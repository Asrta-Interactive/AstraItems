package makeevrserg.empireprojekt.random_items.data

import com.google.gson.annotations.SerializedName

data class NamedMinMax(
    @SerializedName("chance")
    val chance: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("amount_min")
    val amountMin: Double,
    @SerializedName("amount_max")
    val amountMax: Double
)