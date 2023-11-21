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

    private var facultyFullTitle: String? = null
    private var facultyShortTitle: String? = null
    private var committeeChairmanPosition: String? = null
    private var commissioner1Position: String? = null
    private var commissioner2Position: String? = null

    private var groupCode: String? = null
    private var groupFullTitle: String? = null


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

            facultyFullTitle = properties.getProperty("facultyFullTitle")
            facultyShortTitle = properties.getProperty("facultyShortTitle")
            committeeChairmanPosition = properties.getProperty("committeeChairmanPosition")
            commissioner1Position = properties.getProperty("commissioner1Position")
            commissioner2Position = properties.getProperty("commissioner2Position")


            groupCode = properties.getProperty("groupCode")
            groupFullTitle = properties.getProperty("groupFullTitle")
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


        properties.setProperty("facultyFullTitle", facultyFullTitle ?: "")
        properties.setProperty("facultyShortTitle", facultyShortTitle ?: "")
        properties.setProperty("committeeChairmanPosition", committeeChairmanPosition ?: "")
        properties.setProperty("commissioner1Position", commissioner1Position ?: "")
        properties.setProperty("commissioner2Position", commissioner2Position ?: "")

        properties.setProperty("groupCode", groupCode ?: "")
        properties.setProperty("groupFullTitle", groupFullTitle ?: "")

        return try {
            FileOutputStream(settingsFile).use { fileOutputStream ->
                properties.store(fileOutputStream, "DONT EDIT THIS FILE MANUALLY")
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



    fun getFacultyFullTitle(): String? = facultyFullTitle
    fun getFacultyShortTitle(): String? = facultyShortTitle
    fun getCommitteeChairmanPosition(): String? = committeeChairmanPosition
    fun getCommissioner1Position(): String? = commissioner1Position
    fun getCommissioner2Position(): String? = commissioner2Position


    fun setFacultyFullTitle(facultyTitle: String) {
        this.facultyFullTitle = facultyTitle
        val result = saveSettings()
        println(result)
    }

    fun setFacultyShortTitle(facultyTitle: String) {
        this.facultyShortTitle = facultyTitle
        val result = saveSettings()
        println(result)
    }

    fun setCommitteeChairmanPosition(committeeChairmanPosition: String) {
        this.committeeChairmanPosition = committeeChairmanPosition
        val result = saveSettings()
        println(result)
    }

    fun setCommissioner1Position(commissioner1Position: String) {
        this.commissioner1Position = commissioner1Position
        val result = saveSettings()
        println(result)
    }

    fun setCommissioner2Position(commissioner2Position: String) {
        this.commissioner2Position = commissioner2Position
        val result = saveSettings()
        println(result)
    }

    fun getGroupCode(): String? = groupCode
    fun getGroupFullTitle(): String? = groupFullTitle

    fun setGroupCode(groupCode: String) {
        this.groupCode = groupCode
        val result = saveSettings()
        println(result)
    }

    fun setGroupFullTitle(groupFullTitle: String) {
        this.groupFullTitle = groupFullTitle
        val result = saveSettings()
        println(result)
    }
}