package com.astrainteractive.empire_items.credit

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PlaceholderHook: PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "embank"
    }

    override fun getAuthor(): String {
        return "makeevrserg"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        player?:return null
        if (params.equals("bank_amount",ignoreCase = true))
            return BankAPI.getBankAmount(player.player).toString()

        if (params.equals("credit_amount",ignoreCase = true))
            return (CreditAPI.getCreditAmount(player.player)*EmpireCredit.config.creditTax).toString()

        return null

    }

}