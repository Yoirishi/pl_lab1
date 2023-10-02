package po01.docs

import org.apache.poi.ss.usermodel.CellType
import po01.excelparser.enums.DisciplineType

interface DisciplineRow {
    abstract fun getStructure(): List<CellType>
    abstract fun getType(): DisciplineType
}