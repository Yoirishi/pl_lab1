package po01.excelparser

import jakarta.inject.Singleton
import po01.structures.Commissioner

@Singleton
class AnnexComposition(
    private val settingsManager: SettingsManager
) {
    val facultyFullNameTemplateValue = "FACULTYFULLNAME"
    val studentFullNameTemplateValue = "STUDENTFULLNAME"
    val groupShortNameTemplateValue = "GROUPSHORTNAME"
    val groupFullNameTemplateValue = "GROUPFULLNAME"
    val groupCodeTemplateValue = "GROUPCODE"

    lateinit var facultyFullActualTitle: String
    lateinit var groupFullNameActualValue: String
    lateinit var groupCodeActualValue: String

    fun loadActualPropertiesFromSettings() {
        facultyFullActualTitle = settingsManager.getFacultyFullTitle() ?: "не указано"
        groupFullNameActualValue = settingsManager.getGroupFullTitle() ?: "не указано"
        groupCodeActualValue = settingsManager.getGroupCode() ?: "не указано"
    }
}