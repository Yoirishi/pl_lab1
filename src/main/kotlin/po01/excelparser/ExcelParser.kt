package po01.excelparser

import jakarta.inject.Singleton
import po01.excelparser.enums.StudentProcess
import po01.excelparser.enums.TargetCourse

@Singleton
class ExcelParser(
    private val studentParser: StudentParser,
    private val wpParser: WPParser,
    private val tableBuilder: MainDocXTableBuilder
) {
    fun start(
        gcPath: String,
        planPaths: Collection<String>,
        outputFolder: String,
        processType: StudentProcess,
        targetCourse: TargetCourse
    ) {
        val gradeControls = studentParser.parse(gcPath)
        val workplan = wpParser.parse(planPaths)
        tableBuilder.build(gradeControls, workplan, outputFolder, processType, targetCourse)
    }
}