package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber
import structures.GradeControl
import structures.WorkPlan
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger

@Singleton
class WordDocumentTableBuilder(
    private val wordRowBuilder: WordRowBuilder
) {
    private val START_INDEX = 17

    fun build(gradeControls: MutableList<GradeControl>, workPlan: WorkPlan)
    {
        val templateFileStream = object {}.javaClass.getResourceAsStream("/template.docx")
        val doc = XWPFDocument(templateFileStream)

        val table = doc.tables[0]

        var rowIndex = START_INDEX //index of row in table to insert parsed data

        val upToSemester = wordRowBuilder.getMaxSemester(gradeControls)

        val creditHoursBySemesterNumberInGc: MutableList<Int> = mutableListOf()
        val creditHoursBySemesterNumberInWp: MutableList<Int> = mutableListOf()


        for (semesterNumber in 0 until upToSemester) {
            creditHoursBySemesterNumberInGc.add(0)
            creditHoursBySemesterNumberInWp.add(0)

            val semesterWP = workPlan.semesters[semesterNumber]
            for (discipline in semesterWP.disciplines) {
                val wordRowToInsert = wordRowBuilder.build(discipline.value, (semesterNumber+1), gradeControls)

                if (wordRowToInsert != null) {
                    val newRow = table.insertNewTableRow(rowIndex)
                    for (col in 0 until 9) {
                        val newCell = newRow.createCell()
                        val paragraph = newCell.addParagraph()
                        val run = paragraph.createRun()
                        newCell.removeParagraph(0)
                        run.setText("")
                        run.fontSize = 10
                        run.fontFamily = "Times New Roman"
                        val tc = newCell.ctTc
                        val tcPr = tc.addNewTcPr()
                        val ctDecimalNumber = CTDecimalNumber.Factory.newInstance()

                        when (col)
                        {
                            0 -> {
                                run.setText(wordRowToInsert.wpDisciplineTitle)
                                ctDecimalNumber.`val` = BigInteger.valueOf(5)
                            }
                            1 -> {
                                run.setText(wordRowToInsert.wpSemester)
                            }
                            2 -> {
                                run.setText(wordRowToInsert.wpCreditHourQuantity)
                                ctDecimalNumber.`val` = BigInteger.valueOf(3)
                            }
                            3 -> {
                                run.setText(wordRowToInsert.wpGradeType)
                                ctDecimalNumber.`val` = BigInteger.valueOf(3)
                            }
                            4 -> {
                                run.setText(wordRowToInsert.gcDisciplineTitle)
                                ctDecimalNumber.`val` = BigInteger.valueOf(6)
                            }
                            5 -> {
                                run.setText(wordRowToInsert.gcCreditHourQuantity)
                                ctDecimalNumber.`val` = BigInteger.valueOf(2)
                            }
                            6 -> {
                                run.setText(wordRowToInsert.gcGradeType)
                            }
                            7 -> {
                                run.setText(wordRowToInsert.gcGradeResult)
                            }
                            8 -> {
                                run.setText(wordRowToInsert.gcFinalGrade)
                                ctDecimalNumber.`val` = BigInteger.valueOf(5)
                            }
                        }
                        tcPr.gridSpan = ctDecimalNumber
                    }

                    creditHoursBySemesterNumberInGc[semesterNumber] += wordRowToInsert.gcCreditHourQuantity.toInt()
                    creditHoursBySemesterNumberInWp[semesterNumber] += wordRowToInsert.wpCreditHourQuantity.toInt()


                    rowIndex++
                }
            }
        }

        rowIndex ++
        for (semesterNumber in 0 until creditHoursBySemesterNumberInWp.size)
        {
            val footerString = "за ${semesterNumber+1} семестр"
            val newRow = table.insertNewTableRow(rowIndex)

            for (col in 0 until 6) {
                val newCell = newRow.createCell()
                val paragraph = newCell.addParagraph()
                val run = paragraph.createRun()
                newCell.removeParagraph(0)
                run.setText("")
                run.fontFamily = "Times New Roman"
                run.fontSize = 9
                run.isItalic = true
                val tc = newCell.ctTc
                val tcPr = tc.addNewTcPr()
                val ctDecimalNumber = CTDecimalNumber.Factory.newInstance()

                paragraph.alignment = ParagraphAlignment.LEFT

                when (col)
                {
                    0 -> {
                        run.setText(footerString)
                        ctDecimalNumber.`val` = BigInteger.valueOf(5)
                    }
                    1 -> {
                        run.setText("")
                    }
                    2 -> {
                        paragraph.alignment = ParagraphAlignment.CENTER
                        run.setText(creditHoursBySemesterNumberInWp[semesterNumber].toString())
                        ctDecimalNumber.`val` = BigInteger.valueOf(6)
                    }
                    3 -> {
                        run.setText(footerString)
                        ctDecimalNumber.`val` = BigInteger.valueOf(6)
                    }
                    4 -> {
                        paragraph.alignment = ParagraphAlignment.CENTER
                        run.setText(creditHoursBySemesterNumberInGc[semesterNumber].toString())
                        ctDecimalNumber.`val` = BigInteger.valueOf(4)
                    }
                    5 -> {
                        run.setText("")
                        ctDecimalNumber.`val` = BigInteger.valueOf(5)
                    }
                }
                tcPr.gridSpan = ctDecimalNumber
            }


            rowIndex++
        }


        val out = FileOutputStream("${gradeControls[0].student.name} - ${gradeControls[0].student.group}.docx")
        doc.write(out)
        out.close()
        templateFileStream.close()
        doc.close()
    }
}