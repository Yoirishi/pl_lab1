package po01.decl

import org.apache.poi.ss.usermodel.CellType

interface DisciplineRow {
    abstract fun getStructure(): List<CellType>
    abstract fun getType(): DisciplineType
}