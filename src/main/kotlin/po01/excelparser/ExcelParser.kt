package po01.excelparser

import jakarta.inject.Singleton

@Singleton
class ExcelParser(
    private val studentParser: StudentParser,
    private val wpParser: WPParser,
    private val tableBuilder: DocXTableBuilder
) {
    fun start(gcPath: String, planPaths: Collection<String>, outputFolder: String) {
        val gradeControls = studentParser.parse(gcPath)
        val workplan = wpParser.parse(planPaths)
        tableBuilder.build(gradeControls, workplan, outputFolder)
    }
}