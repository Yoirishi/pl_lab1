package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.InputStream

@Singleton
class ExcelConsolePrinter {
    fun printContent(pathToFile: String) {
        val inputStream: InputStream = File(pathToFile).inputStream()

        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)  // Get first sheet
        val rowIterator = sheet.iterator()

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val cellIterator = row.cellIterator()

            while (cellIterator.hasNext()) {
                val cell = cellIterator.next()
                when (cell.cellType) {
                    CellType._NONE -> TODO()
                    CellType.NUMERIC -> print("${cell.cellType} \t")
                    CellType.STRING -> print("${cell.cellType} \t")
                    CellType.FORMULA -> TODO()
                    CellType.BLANK -> print("${cell.cellType} \t")
                    CellType.BOOLEAN -> TODO()
                    CellType.ERROR -> TODO()
                }
            }
            println()
        }
        workbook.close()
    }
}