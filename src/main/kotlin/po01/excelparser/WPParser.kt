package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import po01.structures.DifferentialTestGrade
import po01.structures.WPDiscipline
import po01.structures.WPSemester
import po01.structures.WorkPlan
import java.io.File
import java.io.InputStream

@Singleton
class WPParser {
    val PRACTICE_GRADE = "Дифференцированный зачет"
    val regexpOfDiscipline = """(\W*)(Семестр\s\d, \d*\s[^,:;.]*,\s[^,:;.]*:\s\W*)""".toRegex()
    private val regexpOfDisciplineMeta = """^([А-Я.,-:;]*\d).*""".toRegex()
    private val regexpOfDisciplineDescription = """(\d), (\d*) часов, отчетность: (\W*)""".toRegex()
    private val regexpOfPracticeDescription = """(\d), (\d*) часов""".toRegex()
    private val regexpOfElectiveDescription = """.*элективная.*""".toRegex()

    fun parse(pathsToFile: Collection<String>): WorkPlan {
        val workPlan = WorkPlan(mutableListOf())

        pathsToFile.forEach {pathToFile ->
            val inputStream: InputStream = File(pathToFile).inputStream()
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            val rowIterator = sheet.iterator()

            rowIterator.next() //skip first row with metadata information

            while (rowIterator.hasNext()) {
                val row = rowIterator.next()
                val cellIterator = row.cellIterator()

                var inputString = ""

                var isDisciplineForStudentChoice = false

                var isElectiveDiscipline = false

                while(cellIterator.hasNext()) {
                    val cell = cellIterator.next()
                    when (cell.cellType) {
                        CellType._NONE -> {}
                        CellType.NUMERIC -> {}
                        CellType.STRING -> {
                            val value = cell.stringCellValue

                            if (value.startsWith("$")) isDisciplineForStudentChoice = true

                            if (value.matches(regexpOfElectiveDescription)) isElectiveDiscipline = true

                            if (!value.matches(regexpOfDisciplineMeta) && !value.startsWith("$")) {
                                inputString += value
                                inputString += "\t"
                            }


                            if (value.contains("Семестр")) break
                        }
                        CellType.FORMULA -> {}
                        CellType.BLANK -> {}
                        CellType.BOOLEAN -> {}
                        CellType.ERROR -> {}
                    }

                }

                val disciplineRawInfo = inputString.split("Семестр")
                val title = disciplineRawInfo[0]

                for  (i in 1 until disciplineRawInfo.size) {
                    val disciplineBySemesterInfo = disciplineRawInfo[i]

                    val matchedDisciplineResult = regexpOfDisciplineDescription.find(disciplineBySemesterInfo)

                    matchedDisciplineResult?.let {
                        val (semesterNumber, workHourQuantity, gradeForm) = it.destructured

                        val creditHourQuantity = if(isElectiveDiscipline) 0.0 else (workHourQuantity.toDouble()/36)

                        while (workPlan.semesters.size < semesterNumber.toInt()) {
                            workPlan.semesters.add(WPSemester(hashMapOf()))
                        }
                        workPlan.semesters[(semesterNumber.toInt() - 1)]
                            .disciplines[title] = WPDiscipline(
                            title,
                            workHourQuantity.toDouble(),
                            creditHourQuantity,
                            gradeForm,
                            isDisciplineForStudentChoice,
                            false,
                            semesterNumber.toInt()
                        )
                    } ?: run {
                        val matchedPracticeResult = regexpOfPracticeDescription.find(disciplineBySemesterInfo)
                        matchedPracticeResult?.let {
                            val (semesterNumber, workHourQuantity) = it.destructured

                            while (workPlan.semesters.size < semesterNumber.toInt()) {
                                workPlan.semesters.add(WPSemester(hashMapOf()))
                            }
                            workPlan.semesters[(semesterNumber.toInt() - 1)]
                                .disciplines[title] = WPDiscipline(
                                title,
                                workHourQuantity.toDouble(),
                                (workHourQuantity.toDouble()/36),
                                PRACTICE_GRADE,
                                isDisciplineForStudentChoice,
                                true,
                                semesterNumber.toInt()
                            )
                        }
                    }
                }
            }
        }
        return workPlan
    }
}