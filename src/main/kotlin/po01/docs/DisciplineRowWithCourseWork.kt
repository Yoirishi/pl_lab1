package po01.docs

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType
import po01.excelparser.enums.DisciplineType

@Singleton
class DisciplineRowWithCourseWork : DisciplineRow {
    override fun getStructure(): List<CellType>  = listOf(CellType.STRING, CellType.NUMERIC, CellType.NUMERIC, CellType.STRING, CellType.STRING)
    override fun getType(): DisciplineType = DisciplineType.WITH_COURSE_WORK
}