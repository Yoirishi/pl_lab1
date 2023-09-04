package po01.decl

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType

@Singleton
class StraightDisciplineRow : DisciplineRow {
    override fun getStructure(): List<CellType> = listOf(CellType.STRING, CellType.NUMERIC, CellType.NUMERIC, CellType.STRING)
    override fun getType(): DisciplineType = DisciplineType.EXAM
}