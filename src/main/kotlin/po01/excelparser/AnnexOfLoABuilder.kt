package po01.excelparser

import jakarta.inject.Singleton
import org.apache.poi.xwpf.usermodel.XWPFDocument

@Singleton
class AnnexOfLoABuilder(
    private val annexComposition: AnnexComposition
) {
    fun build(
        studentFullName: String,
        targetGroupShortTitle: String,
        upToSemesterNumber: Int,
        creditHoursBySemesterNumberInWp: MutableList<Int>,
        creditHoursBySemesterNumberInGc: MutableList<Int>
    ) : XWPFDocument
    {
        annexComposition.loadActualPropertiesFromSettings()
        val annexTemplateFileStream = object {}.javaClass.getResourceAsStream("/annexOfLoATemplate.docx")
        val annexDoc = XWPFDocument(annexTemplateFileStream)


        annexDoc.paragraphs[3].runs.forEachIndexed { index, run ->
            val text = run.text()
            val replacedText = text?.replace(annexComposition.facultyFullNameTemplateValue, annexComposition.facultyFullActualTitle)
            if (replacedText != null) {
                run.setText(replacedText, 0)
            }
        }


        val annexStudentFullNameText = annexDoc.paragraphs[4].runs[0].text()
        val annexStudentFullNameReplacedText = annexStudentFullNameText?.replace(annexComposition.studentFullNameTemplateValue, studentFullName)
        if (annexStudentFullNameReplacedText != null) {
            annexDoc.paragraphs[4].runs[0].setText(annexStudentFullNameReplacedText, 0)
        }

        val annexGroupShortTitleText = annexDoc.paragraphs[4].runs[2].text()
        val annexGroupShortTitleReplacedText = annexGroupShortTitleText?.replace(annexComposition.groupShortNameTemplateValue, targetGroupShortTitle)
        if (annexGroupShortTitleReplacedText != null) {
            annexDoc.paragraphs[4].runs[2].setText(annexGroupShortTitleReplacedText, 0)
        }

        val annexGroupCodeText = annexDoc.paragraphs[4].runs[4].text()
        val annexGroupCodeReplacedText = annexGroupCodeText?.replace(annexComposition.groupCodeTemplateValue, annexComposition.groupCodeActualValue)
        if (annexGroupCodeReplacedText != null) {
            annexDoc.paragraphs[4].runs[4].setText(annexGroupCodeReplacedText, 0)
        }


        val annexGroupFullTitleText = annexDoc.paragraphs[5].runs[1].text()
        val annexGroupFullTitleReplacedText = annexGroupFullTitleText?.replace(annexComposition.groupFullNameTemplateValue, annexComposition.groupFullNameActualValue)
        if (annexGroupFullTitleReplacedText != null) {
            annexDoc.paragraphs[5].runs[1].setText(annexGroupFullTitleReplacedText, 0)
        }

//        annexDoc.paragraphs.forEachIndexed { index, xwpfParagraph ->
//            xwpfParagraph.runs.forEach { run ->
//                val text = run.text()
//                val a = 4
//                val replacedText = text?.replace(annexComposition.facultyFullNameTemplateValue, "faculty new value")
//                if (replacedText != null) {
//                    run.setText(replacedText, 0)
//                }
//            }
//        }


        val mainTable = annexDoc.tables[0]
        val additionalTable = annexDoc.tables[1]

        for (i in 0 until upToSemesterNumber) {
            val semesterNumber = i + 1
            val courseNumber = (semesterNumber / 2) + (semesterNumber % 2)
        }


        return annexDoc
    }
}