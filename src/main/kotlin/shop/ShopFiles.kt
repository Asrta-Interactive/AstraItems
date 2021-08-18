package shop

import empirelibs.FileManager
import java.io.File

class ShopFiles {
    val config: FileManager =
        FileManager("shop" + File.separator + "config.yml")
    val shop: FileManager =
        FileManager("shop" + File.separator + "shop.yml")
}