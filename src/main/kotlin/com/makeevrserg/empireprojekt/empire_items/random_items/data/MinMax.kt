package makeevrserg.empireprojekt.random_items.data

import com.google.gson.annotations.SerializedName

data class MinMax(
    @SerializedName("amount_min")
    val amountMin:Int,
    @SerializedName("amount_max")
    val amountMax:Int
)
