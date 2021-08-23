package com.makeevrserg.empireprojekt.credit.data

import com.google.gson.annotations.SerializedName
import com.makeevrserg.empireprojekt.credit.EmpireCredit
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import java.math.BigDecimal

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
            EmpireYamlParser.fromYAML<CreditConfig>(
                EmpireCredit.configFile.getConfig(),
                CreditConfig::class.java,
                listOf("config")
            )

    }
}