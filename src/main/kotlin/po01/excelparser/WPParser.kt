package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import structures.WPDiscipline
import structures.WPSemester
import structures.WorkPlan
import java.io.File
import java.io.InputStream

@Singleton
class WPParser {
    val regexpOfDiscipline = """(\W*)(Семестр\s\d, \d*\s[^,:;.]*,\s[^,:;.]*:\s\W*)""".toRegex()
    val regexpOfDisciplineMeta = """^([А-Я.,-:;]*\d).*""".toRegex()
    val regexpOfDisciplineDescription = """(\d), (\d*) часов, отчетность: (\W*)""".toRegex()

    fun parse(pathToFile: String): WorkPlan {
        val workPlan = WorkPlan(mutableListOf())


        val inputStream: InputStream = File(pathToFile).inputStream()
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val rowIterator = sheet.iterator()

        rowIterator.next() //skip first row with metadata information

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val cellIterator = row.cellIterator()

            var inputString = ""

            while(cellIterator.hasNext()) {
                val cell = cellIterator.next()
                when (cell.cellType) {
                    CellType._NONE -> {}
                    CellType.NUMERIC -> {}
                    CellType.STRING -> {
                        val value = cell.stringCellValue
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

                val matchedResult = regexpOfDisciplineDescription.find(disciplineBySemesterInfo)

                matchedResult?.let {
                    val (semesterNumber, workHourQuantity, gradeForms) = it.destructured
                    val splitGradeForms = gradeForms.split(", ")

                    while (workPlan.semesters.size < semesterNumber.toInt()) {
                        workPlan.semesters.add(WPSemester(hashMapOf()))
                    }
                    for (gradeForm in splitGradeForms)
                    {
                        if (!gradeForm.contains("Курсо"))
                        {
                            workPlan.semesters[(semesterNumber.toInt() - 1)]
                                .disciplines.set(
                                    title,
                                    WPDiscipline(
                                        title,
                                        workHourQuantity.toDouble(),
                                        (workHourQuantity.toDouble()/36),
                                        gradeForm
                                    )
                                )
                        }
                    }
                }
            }
        }
        return workPlan
    }
}