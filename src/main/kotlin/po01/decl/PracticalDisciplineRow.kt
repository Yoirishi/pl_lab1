package po01.decl

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType

@Singleton
class PracticalDisciplineRow : DisciplineRow {
    override fun getStructure(): List<CellType> = listOf(CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING)
    override fun getType(): DisciplineType = DisciplineType.TEST
}