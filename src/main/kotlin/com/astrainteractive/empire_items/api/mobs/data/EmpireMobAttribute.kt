package com.astrainteractive.empire_items.api.mobs.data

import com.astrainteractive.empire_items.api.utils.getDoubleOrNull
import org.bukkit.configuration.ConfigurationSection
import kotlin.random.Random

data class EmpireMobAttribute(
    val attribute: String,
    private val _amount: Double?,
    private val minAmount: Double?,
    private val maxAmount: Double?
) {
    val amount: Double
        get() {
            if (_amount != null)
                return _amount
            return Random(System.currentTimeMillis()).nextDouble(minAmount!!, maxAmount!!+0.0001)
        }

    companion object {
        fun get(s: ConfigurationSection?): List<EmpireMobAttribute> {
            return s?.getKeys(false)?.mapNotNull { key->
                fromSubSection(s.getConfigurationSection(key))
            }?: listOf()
        }
        fun fromSubSection(s: ConfigurationSection?): EmpireMobAttribute? {
            s ?: return null
            val attribute = s.getString("name") ?: s.name
            val minAmount = s.getDoubleOrNull("min")
            val maxAmount = s.getDoubleOrNull("max")
            val amount = s.getDoubleOrNull("amount")?:s.parent?.getDoubleOrNull(attribute)
            if (minAmount == null && maxAmount == null)
                return EmpireMobAttribute(
                    attribute = attribute,
                    _amount = amount ?: return null,
                    minAmount = minAmount,
                    maxAmount = maxAmount
                )
            else return EmpireMobAttribute(
                attribute = attribute,
                _amount = null,
                minAmount = minAmount,
                maxAmount = maxAmount
            )
        }
    }
}