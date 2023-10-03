package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.xwpf.usermodel.XWPFDocument
import po01.excelparser.enums.StudentProcess
import java.lang.StringBuilder

@Singleton
class DocumentRequisitesReplacer(
    private val documentComposition: DocumentComposition
) {
    fun replace(document: XWPFDocument, processType: StudentProcess, studentFullName: String) {
        documentComposition.setProcessTypeActualValue(processType)
        for (paragraph in document.paragraphs) {
            paragraph.runs.forEach { run ->
                val text = run.text()
                val replacedText = text?.replace(documentComposition.facultyTemplateValue, documentComposition.facultyActualValue)
                if (replacedText != null) {
                    run.setText(replacedText, 0)
                }
            }
        }

        document.tables.forEachIndexed { indexTable, table ->
            table.rows.forEachIndexed { indexRow, row ->
                row.tableCells.forEachIndexed { indexCell, cell ->
                    cell.paragraphs.forEachIndexed { indexCellParagraph, paragraph ->
                        paragraph.runs.forEachIndexed { indexRun, run ->
//                            val text = run.text()
//                            val data = "indexTable = $indexTable && indexRow = $indexRow && indexCell = $indexCell && indexCellParagraph = $indexCellParagraph && indexRun = $indexRun"
//                            val a  = 4


                            if (indexTable == 0 && indexRow == 2 && indexCell == 0 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val replacedText = text?.replace(documentComposition.facultyTemplateValue, documentComposition.facultyActualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                            if (indexTable == 0 && indexRow == 3 && indexCell == 3 && indexCellParagraph == 1 && indexRun == 0) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }
                            if (indexTable == 0 && indexRow == 4 && indexCell == 3 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }
                            if (indexTable == 0 && indexRow == 5 && indexCell == 5 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                            if (indexTable == 0 && indexRow == 7 && indexCell == 1 && indexCellParagraph == 0 && indexRun == 0) {
                                val text = run.text()
                                val replacedText = text?.replace(documentComposition.studentFullNameTemplateValue, handleStringWithMaxWidth(studentFullName, documentComposition.maxStudentFullNameLength))
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                            if (indexTable == 0 && indexRow == 9 && indexCell == 0 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val replacedText = text?.replace(documentComposition.processTypeTemplateValue, handleStringWithMaxWidth(documentComposition.processTypeActualValue, documentComposition.maxProcessTypeLength))
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                            if (indexTable == 0 && indexRow == 22 && indexCell == 0 && indexCellParagraph == 0 && indexRun == 3) {
                                val text = run.text()
                                val replacedText = text?.replace(
                                    documentComposition.studentShortNameTemplateValue,
                                    handleStringWithMaxWidth(
                                        studentShortNameFormat(studentFullName),
                                        documentComposition.maxStudentShortNameLength
                                    )
                                )
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                            if (indexTable == 0 && indexRow == 27 && indexCell == 3 && indexCellParagraph == 1 && indexRun == 0) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }
                            if (indexTable == 0 && indexRow == 28 && indexCell == 3 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }
                            if (indexTable == 0 && indexRow == 29 && indexCell == 5 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    fun handleStringWithMaxWidth(string: String, maxLength: Int): String {
        val stringCurrentLength = string.length
        if (stringCurrentLength >= maxLength) return string
        else {
            val sideUnderscoreFieldSize = ((maxLength - stringCurrentLength)/2)
            val resultBuilder = StringBuilder()
            repeat(sideUnderscoreFieldSize) {
                resultBuilder.append('_')
            }
            resultBuilder.append(string)
            repeat(sideUnderscoreFieldSize) {
                resultBuilder.append('_')
            }
            return resultBuilder.toString()
        }
    }

    fun studentShortNameFormat(studentFullName: String): String {
        val splitStudentName = studentFullName.split(' ')
        val resultBuilder = StringBuilder(splitStudentName[0])
        for (i in 1 until splitStudentName.size) {
            resultBuilder.append(' ')
            resultBuilder.append(splitStudentName[i][0])
            resultBuilder.append('.')
        }
        return resultBuilder.toString()
    }
}
