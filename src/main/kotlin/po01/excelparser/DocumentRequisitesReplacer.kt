package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.xwpf.usermodel.XWPFDocument
import po01.excelparser.enums.StudentProcess

@Singleton
class DocumentRequisitesReplacer(
    private val documentComposition: DocumentComposition
) {
    fun replace(document: XWPFDocument, processType: StudentProcess) {
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

                            if (indexTable == 0 && indexRow == 9 && indexCell == 0 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val replacedText = text?.replace(documentComposition.processTypeTemplateValue, documentComposition.processTypeActualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }

                            if (indexTable == 0 && indexRow == 29 && indexCell == 3 && indexCellParagraph == 1 && indexRun == 0) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }
                            if (indexTable == 0 && indexRow == 30 && indexCell == 3 && indexCellParagraph == 0 && indexRun == 1) {
                                val text = run.text()
                                val commissioner = documentComposition.commissioners.first { it.templateValue == text }
                                val replacedText = text?.replace(commissioner.templateValue, commissioner.actualValue)
                                if (replacedText != null) {
                                    run.setText(replacedText, 0)
                                }
                            }
                            if (indexTable == 0 && indexRow == 31 && indexCell == 5 && indexCellParagraph == 0 && indexRun == 1) {
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
}
