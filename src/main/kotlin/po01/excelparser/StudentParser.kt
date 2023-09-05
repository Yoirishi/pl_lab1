package po01.excelparser

import po01.decl.StudentRow
import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import po01.decl.DisciplineRow
import po01.decl.DisciplineType
import po01.decl.PracticalDisciplineRow
import po01.structures.*
import java.io.File
import java.io.InputStream

@Singleton
class StudentParser(
    private val disciplineRows: Collection<DisciplineRow>,
    private val studentRow: StudentRow,
    private val differentialTestChecker: DifferentialTestChecker
) {
    private val regexpOfElectiveDescription = """.*элективная.*""".toRegex()
    fun parse(pathToFile: String): MutableList<GradeControl> {


        val structure = getStructure(pathToFile)
        val gradeControls: MutableList<GradeControl> = mutableListOf()

        val inputStream: InputStream = File(pathToFile).inputStream()
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val rowIterator = sheet.iterator()

        structure.forEach { plan ->
            val row = rowIterator.next()
            val cellIterator = row.cellIterator()

            if (compareLists(plan, studentRow.getStructure()))
            {

                val name = cellIterator.next().stringCellValue
                val group = cellIterator.next().stringCellValue
                val semester = cellIterator.next().stringCellValue

                val student = Student(name, group, semester)
                gradeControls.add(GradeControl(student, mutableListOf()))

            } else {
                disciplineRows.forEach { row ->
                    if (compareLists(plan, row.getStructure()))
                    {
                        val title = cellIterator.next().stringCellValue
                        val workHourQuantity = when(row)
                        {
                            is PracticalDisciplineRow -> {
                                cellIterator.next().stringCellValue.toDouble()
                            }

                            else -> {
                                cellIterator.next().numericCellValue
                            }
                        }
                        cellIterator.next()

                        val creditHourQuantity =  if(title.matches(regexpOfElectiveDescription)) 0.0 else (workHourQuantity/36)

                        when (row.getType()) {
                            DisciplineType.EXAM,
                            DisciplineType.TEST -> {
                                val cell = cellIterator.next()
                                val grade = when (cell.columnIndex)
                                {
                                    4 -> ExamGrade(cell.stringCellValue)
                                    5 -> {
                                        val gradeResult = cell.stringCellValue
                                        val isTestDifferential = differentialTestChecker.checkIsTestDifferential(gradeResult)
                                        if (isTestDifferential) {
                                            DifferentialTestGrade(gradeResult)
                                        } else {
                                            TestGrade(gradeResult)
                                        }
                                    }
                                    else -> throw Exception("Unknown grade type in student grade control list: grade column index is ${cell.columnIndex}")
                                }

                                gradeControls[gradeControls.size-1].disciplines.add(Discipline(title, workHourQuantity, creditHourQuantity, grade))
                            }
                            DisciplineType.WITH_COURSE_WORK -> {
                                val firstCell = cellIterator.next()
                                val secondCell = cellIterator.next()

                                val grade = when (firstCell.columnIndex)
                                {
                                    4 -> ExamWithCWGrades(firstCell.stringCellValue, secondCell.stringCellValue)
                                    5 -> {
                                        val gradeResult = firstCell.stringCellValue
                                        val isTestDifferential = differentialTestChecker.checkIsTestDifferential(gradeResult)
                                        if (isTestDifferential) {
                                            DifferentialTestWithCWGrades(gradeResult, secondCell.stringCellValue)
                                        } else {
                                            TestWithCWGrades(gradeResult, secondCell.stringCellValue)
                                        }
                                    }
                                    else -> throw Exception("Unknown grade type in student grade control list: grade column index is ${firstCell.columnIndex}")
                                }

                                gradeControls[gradeControls.size-1].disciplines.add(Discipline(title, workHourQuantity, creditHourQuantity, grade))
                            }
                        }
                    }
                }
            }
        }
        workbook.close()

        return gradeControls
    }


    private fun getStructure(pathToFile: String): MutableList<MutableList<CellType>> {
        val inputStream: InputStream = File(pathToFile).inputStream()
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val rowIterator = sheet.iterator()
        val rows : MutableList<MutableList<CellType>> = mutableListOf()

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val cellIterator = row.cellIterator()

            val rowCellTypes: MutableList<CellType> = mutableListOf()

            while (cellIterator.hasNext()) {
                val cell = cellIterator.next()
                when (cell.cellType)
                {
                    CellType._NONE -> TODO()
                    CellType.NUMERIC -> rowCellTypes.add(CellType.NUMERIC)
                    CellType.STRING -> rowCellTypes.add(CellType.STRING)
                    CellType.FORMULA -> TODO()
                    CellType.BLANK -> TODO()
                    CellType.BOOLEAN -> TODO()
                    CellType.ERROR -> TODO()
                }
            }
            rows.add(rowCellTypes)
        }
        workbook.close()
        return rows
    }

    private fun compareLists(list1: List<CellType>, list2: List<CellType>): Boolean {
        if (list1.size != list2.size) {
            return false
        }

        for (i in list1.indices) {
            if (list1[i] != list2[i]) {
                return false
            }
        }

        return true
    }
}