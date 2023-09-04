package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.xmlbeans.XmlCursor
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STVerticalAlignRunImpl
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc
import po01.structures.WPDiscipline
import java.math.BigInteger

@Singleton
class ProgramDifferenceDocXBuilder {
    fun build(studentName: String, disciplinesProgramDifference: MutableList<WPDiscipline>): XWPFDocument {
        val pdTemplateFileStream = object {}.javaClass.getResourceAsStream("/programDifferenceTemplate.docx")
        val pdDoc = XWPFDocument(pdTemplateFileStream)

        val paragraphs = pdDoc.paragraphs

        if (paragraphs.isNotEmpty()) {
            val nameParagraph = paragraphs[0]
            nameParagraph.alignment = ParagraphAlignment.CENTER
            val run = nameParagraph.createRun()
            run.addCarriageReturn()
            run.fontSize = 14
            run.setText(studentName)
            run.fontFamily = "Times New Roman"
            run.isBold = true

        }
        val table = pdDoc.tables[0]
        var indexToInsert = 0 //insert on second row

        var creditHourTotalQuantity = 0


        for (discipline in disciplinesProgramDifference) {
            indexToInsert++
            val newRow = table.insertNewTableRow(indexToInsert)
            for (columnNumber in 0 until 5) {
                val newCell = newRow.createCell()
                newCell.verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
                val paragraph = newCell.addParagraph()
                newCell.removeParagraph(0)
                val run = paragraph.createRun()

                run.setText("")
                run.fontSize = 12
                run.fontFamily = "Times New Roman"

                when (columnNumber) {
                    0 -> {
                        paragraph.alignment = ParagraphAlignment.LEFT
                        run.setText(discipline.title)
                    }
                    1 -> {
                        paragraph.alignment = ParagraphAlignment.CENTER
                        run.setText(discipline.semesterNumber.toString())
                    }
                    2 -> {
                        paragraph.alignment = ParagraphAlignment.CENTER
                        run.setText(discipline.creditHourQuantity.toInt().toString())
                    }
                    3 -> {
                        paragraph.alignment = ParagraphAlignment.CENTER
                        run.setText(discipline.gradeForm.trim())
                    }
                    4 -> {
                        paragraph.alignment = ParagraphAlignment.CENTER
                        run.setText("")
                    }
                }
            }
            creditHourTotalQuantity += discipline.creditHourQuantity.toInt()
        }

        indexToInsert++
        val newRow = table.insertNewTableRow(indexToInsert)

        for (columnNumber in 0 until 3) {
            val newCell = newRow.createCell()
            newCell.verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
            val paragraph = newCell.addParagraph()
            newCell.removeParagraph(0)
            val run = paragraph.createRun()
            val tc = newCell.ctTc
            val tcPr = tc.addNewTcPr()
            val ctDecimalNumber = CTDecimalNumber.Factory.newInstance()

            run.setText("")
            run.fontSize = 12
            run.fontFamily = "Times New Roman"
            run.isBold = true
            run.isItalic = true
            paragraph.alignment = ParagraphAlignment.CENTER

            when (columnNumber) {
                0 -> {
                    run.setText("Итого")
                    ctDecimalNumber.`val` = BigInteger.valueOf(2)
                }
                1 -> {
                    run.setText(creditHourTotalQuantity.toString())
                }
                2 -> {
                    run.setText("")
                    ctDecimalNumber.`val` = BigInteger.valueOf(2)
                }
            }
            tcPr.gridSpan = ctDecimalNumber
        }

        return pdDoc
    }
}