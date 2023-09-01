package decl

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType

@Singleton
class WorkPlanDisciplineRow  {
    fun getStructure(): List<CellType> = listOf(CellType.BLANK, CellType.BLANK, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.BLANK, CellType.STRING, CellType.BLANK, CellType.BLANK)
}