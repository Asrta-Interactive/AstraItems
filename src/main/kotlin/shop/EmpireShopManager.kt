package shop

import shop.data.ShopItem

class EmpireShopManager {
    companion object {
        public lateinit var instance: EmpireShopManager
        public val files: ShopFiles = ShopFiles()
    }

    public val shops = ShopItem.new()

    init {
        instance = this
    }
}