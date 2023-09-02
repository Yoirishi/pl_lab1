package po01.excelparser

import po01.decl.WordRowToInsert
import jakarta.inject.Singleton
import po01.structures.GradeControl
import po01.structures.WPDiscipline


@Singleton
class DocXRowBuilder {

    val regexpToGetSemester = """\W*(\d*)""".toRegex()


    fun build(
        discipline: WPDiscipline,
        semesterNumber: Int,
        gradeControls: MutableList<GradeControl>
    ): WordRowToInsert? {
        for (gradeControl in gradeControls)
        {
            val matchResult = regexpToGetSemester.find(gradeControl.student.semester)
            var gcSemesterNumber = 0
            matchResult?.let {
                val (number) = it.destructured
                gcSemesterNumber = number.toInt()
            } ?: run {}

            if (gcSemesterNumber == semesterNumber) {
                for (gcDiscipline in gradeControl.disciplines) {
                    if (gcDiscipline.title == discipline.title.trim())
                    {
                        val finalGrade = if ((discipline.creditHourQuantity * 2)/3 <= gcDiscipline.creditHourQuantity ) {
                            gcDiscipline.grade
                        } else {
                            ""
                        }

                        val gcGradeType = if (gcDiscipline.grade == "зачет" || gcDiscipline.grade == "незачет")
                        {
                            "Зачет"
                        } else {
                            discipline.gradeForm
                        }

                        return WordRowToInsert(
                            discipline.title,
                            semesterNumber.toString(),
                            discipline.creditHourQuantity.toInt().toString(),
                            discipline.gradeForm,
                            gcDiscipline.title,
                            gcDiscipline.creditHourQuantity.toInt().toString(),
                            gcGradeType,
                            gcDiscipline.grade,
                            finalGrade
                        )
                    }
                }
                return if (!discipline.isDisciplineForStudentChoice) {
                    WordRowToInsert(
                        discipline.title,
                        semesterNumber.toString(),
                        discipline.creditHourQuantity.toString(),
                        discipline.gradeForm,
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                } else {
                    null
                }

            }
        }
        return null
    }

    fun getMaxSemester(gradeControls: MutableList<GradeControl>): Int {
        var result = 0
        for (gradeControl in gradeControls) {
            val matchResult = regexpToGetSemester.find(gradeControl.student.semester)
            matchResult?.let {
                val (number) = it.destructured
                if (number.toInt() > result) {
                    result = number.toInt()
                }
            } ?: run {}
        }
        return result
    }
}