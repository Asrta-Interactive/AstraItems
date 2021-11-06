package com.astrainteractive.empireprojekt.empire_items.util.resource_pack

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Zipper {
    fun zipAll(folderPath: String, outputFilePath: String): Boolean {
        val fos = FileOutputStream(outputFilePath)
        val zipOut = ZipOutputStream(fos)
        try {
            var fileToZip = File(folderPath + File.separator + "assets")
            zipFile(fileToZip, fileToZip.name, zipOut)
            fileToZip = File(folderPath + File.separator + "pack.mcmeta")
            zipFile(fileToZip, fileToZip.name, zipOut)
            fileToZip = File(folderPath + File.separator + "pack.png")
            zipFile(fileToZip, fileToZip.name, zipOut)

            zipOut.close()
            fos.close()
            return true
        } catch (e: IOException) {
            return false
        } finally {
            zipOut.close()
            fos.close()
        }
    }

    @Throws(IOException::class)
    private fun zipFile(
        fileToZip: File,
        fileName: String,
        zipOut: ZipOutputStream
    ) {
        if (fileToZip.isHidden) {
            return
        }
        if (fileToZip.isDirectory) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(ZipEntry(fileName))
                zipOut.closeEntry()
            } else {
                zipOut.putNextEntry(ZipEntry("$fileName/"))
                zipOut.closeEntry()
            }
            val children = fileToZip.listFiles()
            for (childFile in children) {
                zipFile(childFile, fileName + "/" + childFile.name, zipOut)
            }
            return
        }
        val fis = FileInputStream(fileToZip)
        val zipEntry = ZipEntry(fileName)
        zipOut.putNextEntry(zipEntry)
        val bytes = ByteArray(1024)
        var length: Int
        while (fis.read(bytes).also { length = it } >= 0) {
            zipOut.write(bytes, 0, length)
        }
        fis.close()

    }
}