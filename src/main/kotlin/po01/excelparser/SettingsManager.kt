package po01.excelparser

import jakarta.inject.Singleton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


@Singleton
class SettingsManager {
    private var lastOutputDirectory: String? = null
    private var lastWpTable: String? = null
    private var lastGcDirectory: String? = null
    private val settingsFile = File("settings.ini")

    init {
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile()
            }
            val properties = Properties()
            FileInputStream(settingsFile).use { fileInputStream ->
                properties.load(fileInputStream)
            }
            lastOutputDirectory = properties.getProperty("lastOutputDirectory")
            lastWpTable = properties.getProperty("lastWpTable")
            lastGcDirectory = properties.getProperty("lastGcDirectory")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getLastOutputDirectory(): String? = lastOutputDirectory

    fun getLastWpTable(): String? = lastWpTable

    fun getLastGcDirectory(): String? = lastGcDirectory

    fun setLastOutputDirectory(lastOutputDirectory: String) {
        this.lastOutputDirectory = lastOutputDirectory
        saveSettings()
    }

    fun setLastWpTable(lastWpTable: String) {
        this.lastWpTable = lastWpTable
        saveSettings()
    }

    fun setLastGcDirectory(lastGcDirectory: String) {
        this.lastGcDirectory = lastGcDirectory
        saveSettings()
    }

    private fun saveSettings(): String {
        val properties = Properties()
        properties.setProperty("lastOutputDirectory", lastOutputDirectory ?: "")
        properties.setProperty("lastWpTable", lastWpTable ?: "")
        properties.setProperty("lastGcDirectory", lastGcDirectory ?: "")

        return try {
            FileOutputStream(settingsFile).use { fileOutputStream ->
                properties.store(fileOutputStream, null)
            }
            "Done"
        } catch (e: IOException) {
            "Error: Could not save settings. File may be locked or missing."
        }
    }
}