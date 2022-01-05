package com.astrainteractive.empire_items.credit.data

import com.astrainteractive.astralibs.AstraYamlParser
import com.google.gson.annotations.SerializedName
import com.astrainteractive.empire_items.credit.EmpireCredit

data class CreditConfig(
    @SerializedName("max_amount")
    val maxAmount: Int,
    @SerializedName("max_credit")
    val maxCredit: Int,
    @SerializedName("credit_tax")
    val creditTax: Double,
    @SerializedName("min_amount_for_credit")
    val minAmountForCredit: Int,
    @SerializedName("credit_withdraw_tax")
    val creditWithdrawTax: Double?

) {
    companion object {
        fun new() =
            AstraYamlParser.parser.configurationSectionToClass<CreditConfig>(EmpireCredit.configFile.getConfig().getConfigurationSection("config"))
    }
}