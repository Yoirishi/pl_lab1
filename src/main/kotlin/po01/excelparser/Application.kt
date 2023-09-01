package po01.excelparser


import io.micronaut.context.BeanContext


fun main(args: Array<String>) {
//	val printer = context.getBean(ExcelConsolePrinter::class.java)
//	printer.printContent(upPath)


	val studentsPath = "оценки.xlsx"
	val wpPath = "учебный план.xls"

	val context = BeanContext.run()
	val studentInfoParser = context.getBean(StudentParser::class.java)

	val gradeControls = studentInfoParser.parse(studentsPath)

	val workPlanParser = context.getBean(WPParser::class.java)

	val workplan = workPlanParser.parse(wpPath)


	val tableBuilder = context.getBean(WordDocumentTableBuilder::class.java)

	tableBuilder.build(gradeControls, workplan)

	context.close()

}

