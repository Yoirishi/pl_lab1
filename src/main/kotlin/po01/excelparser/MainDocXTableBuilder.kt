package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber
import po01.excelparser.enums.StudentProcess
import po01.excelparser.enums.TargetCourse
import po01.structures.GradeControl
import po01.structures.WPDiscipline
import po01.structures.validators.GradeResultValidator
import po01.structures.WorkPlan
import po01.structures.validators.CreditHoursValidator
import po01.structures.validators.DisciplineNotFacultativeValidator
import java.io.FileOutputStream
import java.lang.Exception
import java.math.BigInteger

@Singleton
class MainDocXTableBuilder(
    private val wordRowBuilder: MainDocXRowBuilder,
    private val documentRequisitesReplacer: DocumentRequisitesReplacer,
    private val gradeResultValidator: GradeResultValidator,
    private val disciplineNotFacultativeValidator: DisciplineNotFacultativeValidator,
    private val creditHoursValidator: CreditHoursValidator,
    private val programDifferenceDocXBuilder: ProgramDifferenceDocXBuilder,
    private val annexOfLoABuilder: AnnexOfLoABuilder
) {
    private val startIndex = 17

    fun build(
        gradeControls: MutableList<GradeControl>,
        workPlan: WorkPlan,
        outputFolderPath: String,
        processType: StudentProcess,
        targetCourse: TargetCourse
    )
    {
        val mainTemplateFileStream = object {}.javaClass.getResourceAsStream("/mainTemplate.docx")
        val mainDoc = XWPFDocument(mainTemplateFileStream)

        documentRequisitesReplacer.replace(mainDoc, processType, gradeControls[0].student.name)

        val table = mainDoc.tables[0]

        var rowIndex = startIndex //index of row in table to insert parsed data

//        val upToSemester = wordRowBuilder.getMaxSemester(gradeControls)
        val upToSemester = when (targetCourse) {
            TargetCourse.FIRST -> 1
            TargetCourse.SECOND -> 2
            TargetCourse.THIRD -> 3
            TargetCourse.FOURTH -> 4
            TargetCourse.FIFTH -> 5
            TargetCourse.SIXTH -> 6
            TargetCourse.SEVENTH -> 7
            TargetCourse.EIGHTH -> 8
        }

        val creditHoursBySemesterNumberInGc: MutableList<Int> = mutableListOf()
        val creditHoursBySemesterNumberInWp: MutableList<Int> = mutableListOf()


        val disciplinesProgramDifference: MutableList<WPDiscipline> = mutableListOf()


        for (semesterNumber in 0 until upToSemester) {
            creditHoursBySemesterNumberInGc.add(0)
            creditHoursBySemesterNumberInWp.add(0)

            val semesterWP = workPlan.semesters[semesterNumber]
            for (discipline in semesterWP.disciplines) {

                val isDisciplineNotFacultative = disciplineNotFacultativeValidator.validate(discipline.value.title)
                if (!isDisciplineNotFacultative) continue

                val wordRowToInsert = wordRowBuilder.build(discipline.value, (semesterNumber+1), gradeControls)

                val isGcCreditHourQuantityValid = try {
                    creditHoursValidator.validate(wordRowToInsert!!.gcCreditHourQuantity)
                } catch (_: Exception) {
                    false
                }
                val isWpCreditHourQuantityValid = try {
                    creditHoursValidator.validate(wordRowToInsert!!.wpCreditHourQuantity)
                } catch (_: Exception) {
                    false
                }

                if (wordRowToInsert != null) {
                    val newRow = table.insertNewTableRow(rowIndex)
                    for (col in 0 until 9) {
                        val newCell = newRow.createCell()
                        newCell.verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
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
                                if (isWpCreditHourQuantityValid) {
                                    run.setText(wordRowToInsert.wpCreditHourQuantity)
                                } else {
                                    run.setText("")
                                }
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
                                if (isGcCreditHourQuantityValid) {
                                    run.setText(wordRowToInsert.gcCreditHourQuantity)
                                } else {
                                    run.setText("")
                                }
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

                    if (gradeResultValidator.validate(wordRowToInsert.gcGradeResult) && isGcCreditHourQuantityValid) {
                        creditHoursBySemesterNumberInGc[semesterNumber] += wordRowToInsert.gcCreditHourQuantity.toInt()
                    } else {
                        disciplinesProgramDifference.add(discipline.value)
                    }
                    if (isWpCreditHourQuantityValid) {
                        creditHoursBySemesterNumberInWp[semesterNumber] += wordRowToInsert.wpCreditHourQuantity.toInt()
                    }


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

        rowIndex += 3 //shift to program difference insert place

        disciplinesProgramDifference.forEach {
            val newRow = table.insertNewTableRow(rowIndex)

            for (col in 0 until 5) {
                val newCell = newRow.createCell()
                val paragraph = newCell.addParagraph()
                val run = paragraph.createRun()
                newCell.removeParagraph(0)
                run.setText("")
                run.fontFamily = "Times New Roman"
                run.fontSize = 10
                val tc = newCell.ctTc
                val tcPr = tc.addNewTcPr()
                val ctDecimalNumber = CTDecimalNumber.Factory.newInstance()

                paragraph.alignment = ParagraphAlignment.LEFT

                when (col)
                {
                    0 -> {
                        run.setText(it.title.trim())
                        ctDecimalNumber.`val` = BigInteger.valueOf(8)
                    }
                    1 -> {
                        run.setText(it.semesterNumber.toString())
                        paragraph.alignment = ParagraphAlignment.CENTER
                        ctDecimalNumber.`val` = BigInteger.valueOf(2)
                    }
                    2 -> {
                        run.setText(it.creditHourQuantity.toInt().toString())
                        paragraph.alignment = ParagraphAlignment.CENTER
                        ctDecimalNumber.`val` = BigInteger.valueOf(4)
                    }
                    3 -> {
                        run.setText(it.gradeForm.trim())
                        paragraph.alignment = ParagraphAlignment.CENTER
                        ctDecimalNumber.`val` = BigInteger.valueOf(5)
                    }
                    4 -> {
                        run.setText("")
                        paragraph.alignment = ParagraphAlignment.CENTER
                        ctDecimalNumber.`val` = BigInteger.valueOf(5)
                    }
                }
                tcPr.gridSpan = ctDecimalNumber
            }


            rowIndex++

        }


        val programDifferenceDoc = programDifferenceDocXBuilder.build(gradeControls[0].student.name, disciplinesProgramDifference)
        val annexOfLoADoc = annexOfLoABuilder.build(
            gradeControls[0].student.name,
            gradeControls[0].student.group,
            upToSemester,
            creditHoursBySemesterNumberInWp,
            creditHoursBySemesterNumberInGc
        )

        val finalOutputFolder = if (outputFolderPath.endsWith("/")) {
            outputFolderPath.substring(0, outputFolderPath.length-2)
        } else {
            outputFolderPath
        }
        val mainOut = FileOutputStream("${finalOutputFolder}/${gradeControls[0].student.name} - ${gradeControls[0].student.group}.docx")
        mainDoc.write(mainOut)
        mainOut.close()

        val splitStudentName = gradeControls[0].student.name.split(" ")
        val pdOut = FileOutputStream("${finalOutputFolder}/Аннотированный ИУП ${splitStudentName[0]}.docx")
        programDifferenceDoc.write(pdOut)
        pdOut.close()

        val annexOut = FileOutputStream("${finalOutputFolder}/Приложение к выходу ${splitStudentName[0]}.docx")
        annexOfLoADoc.write(annexOut)
        annexOut.close()

        mainTemplateFileStream?.close()
        mainDoc.close()

        programDifferenceDoc.close()
    }
}