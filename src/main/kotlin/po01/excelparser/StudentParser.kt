package po01.excelparser

import po01.decl.DisciplineRow
import po01.decl.StudentRow
import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import po01.structures.Discipline
import po01.structures.GradeControl
import po01.structures.Student
import java.io.File
import java.io.InputStream

@Singleton
class StudentParser(
    private val disciplineRow: DisciplineRow,
    private val studentRow: StudentRow
) {
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

            } else if (compareLists(plan, disciplineRow.getStructure()))
            {
                val title = cellIterator.next().stringCellValue
                val workHourQuantity = cellIterator.next().numericCellValue
                cellIterator.next()
                val creditHourQuantity = (workHourQuantity/36)
                val grade = cellIterator.next().stringCellValue

                gradeControls[gradeControls.size-1].disciplines.add(Discipline(title, workHourQuantity, creditHourQuantity, grade))
            } else throw Exception("Unknown data")
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