import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.empire_items.EmpirePlugin
import java.io.File

class Utility {
    companion object {
        val testDirectoryPath: String
            get() = File(".").absolutePath.replace(
                    "${File.separator}.",
                    ""
            ) + File.separator + "src" + File.separator + "test" + File.separator + "resources"
        val realPluginFolderPath: String
            get() = EmpirePlugin.instance.dataFolder.toString() + File.separator

        /**
         * Отправляет файлы из [testDirectoryPath] в Mock'нутую директорию сервера [realPluginFolderPath]
         */
        fun sendTestFiles() {
            val finalDestination = AstraLibs.instance.dataFolder.toString() + File.separator
            fun sendFromDir(dirPath: String,parent:String="") {
                File(dirPath).listFiles()?.forEach { file ->
                    file.copyTo(File(finalDestination + File.separator+parent + file.name), overwrite = true)
                    if (file.isDirectory)
                        sendFromDir(file.absolutePath,parent = parent+file.name+File.separator)
                }
            }
            sendFromDir(testDirectoryPath)
        }

        fun getFilesRecursevly(path: String): List<String> = File(path).listFiles()?.map {
            if (it.isFile) listOf(it.absolutePath)
            else getFilesRecursevly(it.absolutePath)
        }?.flatten() ?: listOf<String>()
    }
}