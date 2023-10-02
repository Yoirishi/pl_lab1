package po01.excelparser

import jakarta.inject.Singleton
import po01.excelparser.enums.StudentProcess
import po01.structures.Commissioner


@Singleton
class DocumentComposition(
    private val settingsManager: SettingsManager
) {
    val commissioners: List<Commissioner>
    val facultyTemplateValue = "FTITLE"
    var facultyActualValue = settingsManager.getFacultyTitle() ?: "не указано"

    val processTypeTemplateValue = "PROCESSTYPE"
    var processTypeActualValue = "что то пошло не так_________________________________________________"

    fun setProcessTypeActualValue(type: StudentProcess) {
        processTypeActualValue = when (type) {
            StudentProcess.WithdrawalFromLeaveOfAbsence -> "________________выходе из академического отпуска_______________"
            StudentProcess.Transfer -> "__________________________переводе_____________________________"
            StudentProcess.Reinstatement -> "_________________________восстановлении________________________"
        }
    }

    init {
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
    }

}
