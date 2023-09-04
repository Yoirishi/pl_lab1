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
    private var lastPracticeTable: String? = null
    private var lastGcDirectory: String? = null
    private var parsePracticeWithMainWPCheckboxValue: String? = null
    private var openDirectoryAfterGeneratingCheckboxValue: String? = null
    private val settingsFile = File("settings.ini")

    init {
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile()
            }
            val properties = Properties()
            FileInputStream(settingsFile).use { properties.load(it) }
            lastOutputDirectory = properties.getProperty("lastOutputDirectory")
            lastWpTable = properties.getProperty("lastWpTable")
            lastPracticeTable = properties.getProperty("lastPracticeTable")
            lastGcDirectory = properties.getProperty("lastGcDirectory")
            parsePracticeWithMainWPCheckboxValue = properties.getProperty("parsePracticeWithMainWPCheckboxValue")
            openDirectoryAfterGeneratingCheckboxValue = properties.getProperty("openDirectoryAfterGeneratingCheckboxValue")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getLastOutputDirectory(): String? = lastOutputDirectory

    fun getLastWpTable(): String? = lastWpTable

    fun getLastGcDirectory(): String? = lastGcDirectory

    fun setLastOutputDirectory(lastOutputDirectory: String) {
        this.lastOutputDirectory = lastOutputDirectory
        val result = saveSettings()
        println(result)
    }

    fun setLastWpTable(lastWpTable: String) {
        this.lastWpTable = lastWpTable
        val result = saveSettings()
        println(result)
    }

    fun setLastGcDirectory(lastGcDirectory: String) {
        this.lastGcDirectory = lastGcDirectory
        val result = saveSettings()
        println(result)
    }

    private fun saveSettings(): String {
        val properties = Properties()
        properties.setProperty("lastOutputDirectory", lastOutputDirectory ?: "")
        properties.setProperty("lastWpTable", lastWpTable ?: "")
        properties.setProperty("lastPracticeTable", lastPracticeTable ?: "")
        properties.setProperty("lastGcDirectory", lastGcDirectory ?: "")
        properties.setProperty("openDirectoryAfterGeneratingCheckboxValue", openDirectoryAfterGeneratingCheckboxValue ?: "")
        properties.setProperty("parsePracticeWithMainWPCheckboxValue", parsePracticeWithMainWPCheckboxValue ?: "")

        return try {
            FileOutputStream(settingsFile).use { fileOutputStream ->
                properties.store(fileOutputStream, null)
            }
            "Change saved"
        } catch (e: IOException) {
            "Error: Could not save settings. File may be locked or missing."
        }
    }

    fun getOpenDirectoryAfterGeneratingCheckboxValue() = this.openDirectoryAfterGeneratingCheckboxValue.toBoolean()

    fun getParsePracticeWithMainWPCheckboxValue() = this.parsePracticeWithMainWPCheckboxValue.toBoolean()

    fun setOpenDirectoryAfterGeneratingCheckboxValue(selected: Boolean) {
        this.openDirectoryAfterGeneratingCheckboxValue = selected.toString()
        val result = saveSettings()
        println(result)
    }

    fun setParsePracticeWithMainWPCheckboxValue(selected: Boolean) {
        this.parsePracticeWithMainWPCheckboxValue = selected.toString()
        val result = saveSettings()
        println(result)
    }

    fun getLastPracticeTable() = this.lastPracticeTable

    fun setLastPracticeTable(lastPracticeTable: String) {
        this.lastPracticeTable = lastPracticeTable
        val result = saveSettings()
        println(result)
    }
}