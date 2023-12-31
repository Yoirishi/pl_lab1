package po01.excelparser

import jakarta.inject.Singleton
import po01.excelparser.enums.StudentProcess
import po01.structures.Commissioner


@Singleton
class DocumentComposition(
    private val settingsManager: SettingsManager
) {
    lateinit var commissioners: List<Commissioner>
    val facultyTemplateValue = "FTITLE"
    var facultyActualValue = settingsManager.getFacultyShortTitle() ?: "не указано"

    val studentFullNameTemplateValue = "STUDENTFULLNAME"
    val maxStudentFullNameLength = 58
    val studentShortNameTemplateValue = "STUDENTSHORTNAME"
    val maxStudentShortNameLength = 28

    val processTypeTemplateValue = "PROCESSTYPE"
    val maxProcessTypeLength = 79
    var processTypeActualValue = "что то пошло не так"

    fun setProcessTypeActualValue(type: StudentProcess) {
        processTypeActualValue = when (type) {
            StudentProcess.WithdrawalFromLeaveOfAbsence -> "выходе из академического отпуска"
            StudentProcess.Transfer -> "переводе"
            StudentProcess.Reinstatement -> "восстановлении"
        }
    }

    init {
        loadActualPropertiesFromSettings()
    }

    fun loadActualPropertiesFromSettings() {
        this.commissioners = listOf(
            Commissioner(
                "COMITTECHAIRMAN",
                settingsManager.getCommitteeChairmanPosition() ?: "не указано"
            ),
            Commissioner(
                "COMMISSIONER1",
                settingsManager.getCommissioner1Position() ?: "не указано"
            ),
            Commissioner(
                "COMMISSIONER2",
                settingsManager.getCommissioner2Position() ?: "не указано"
            )
        )
        facultyActualValue = settingsManager.getFacultyShortTitle() ?: "не указано"
    }

}
