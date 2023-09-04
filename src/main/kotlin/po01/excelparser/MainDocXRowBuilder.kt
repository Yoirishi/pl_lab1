package po01.excelparser

import po01.decl.WordRowToInsert
import jakarta.inject.Singleton
import po01.structures.*
import po01.structures.validators.CourseWorkIsProjectValidator


@Singleton
class MainDocXRowBuilder(
    private val courseWorkIsProjectValidator: CourseWorkIsProjectValidator
) {

    private val regexpToGetSemester = """\W*(\d*)""".toRegex()


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
                        val isGradeFormWithProject = courseWorkIsProjectValidator.validate(discipline.gradeForm)
                        val gcGradeType = when (gcDiscipline.grade) {
                            is DifferentialTestGrade,
                            is ExamGrade,
                            is TestGrade -> {
                                when (gcDiscipline.grade.gradeType) {
                                    GradeType.EXAM -> "Экзамен"
                                    GradeType.TEST -> "Зачет"
                                    GradeType.DIFFERENTIAL_TEST -> "Дифференцированный зачет"
                                    GradeType.COURSE_WORK -> throw Exception("Cant handle discipline ${gcDiscipline.title}")
                                }
                            }
                            is ExamWithCWGrades,
                            is DifferentialTestWithCWGrades,
                            is TestWithCWGrades -> {
                                when (gcDiscipline.grade.gradeType) {
                                    GradeType.EXAM -> {
                                        if (isGradeFormWithProject) {
                                            "Экзамен, Курсовой проект"
                                        } else {
                                            "Экзамен, Курсовая работа"
                                        }
                                    }
                                    GradeType.TEST -> {
                                        if (isGradeFormWithProject) {
                                            "Зачет, Курсовой проект"
                                        } else {
                                            "Зачет, Курсовая работа"
                                        }
                                    }
                                    GradeType.DIFFERENTIAL_TEST -> {
                                        if (isGradeFormWithProject) {
                                            "Дифференцированный зачет, Курсовой проект"
                                        } else {
                                            "Дифференцированный зачет, Курсовая работа"
                                        }
                                    }
                                    GradeType.COURSE_WORK -> throw Exception("Cant handle discipline ${gcDiscipline.title}")
                                }
                            }
                        }

                        val gcGrade = when (gcDiscipline.grade) {
                            is DifferentialTestGrade -> gcDiscipline.grade.gradeResult
                            is ExamGrade -> gcDiscipline.grade.gradeResult
                            is TestGrade -> gcDiscipline.grade.gradeResult
                            is ExamWithCWGrades -> "${gcDiscipline.grade.gradeResult}, ${gcDiscipline.grade.cwGradeResult}"
                            is DifferentialTestWithCWGrades -> "${gcDiscipline.grade.gradeResult}, ${gcDiscipline.grade.cwGradeResult}"
                            is TestWithCWGrades -> "${gcDiscipline.grade.gradeResult}, ${gcDiscipline.grade.cwGradeResult}"
                        }

                        val finalGrade = if ((discipline.creditHourQuantity * 2)/3 <= gcDiscipline.creditHourQuantity ) {
                            when (gcDiscipline.grade) {
                                is DifferentialTestGrade -> gcDiscipline.grade.gradeResult
                                is DifferentialTestWithCWGrades -> gcDiscipline.grade.gradeResult
                                is ExamGrade -> gcDiscipline.grade.gradeResult
                                is ExamWithCWGrades -> gcDiscipline.grade.gradeResult
                                is TestGrade -> gcDiscipline.grade.gradeResult
                                is TestWithCWGrades -> gcDiscipline.grade.gradeResult
                            }
                        } else {
                            ""
                        }

                        return WordRowToInsert(
                            discipline.title,
                            semesterNumber.toString(),
                            discipline.creditHourQuantity.toInt().toString(),
                            discipline.gradeForm,
                            gcDiscipline.title,
                            gcDiscipline.creditHourQuantity.toInt().toString(),
                            gcGradeType,
                            gcGrade,
                            finalGrade
                        )
                    }
                }
                return if (!discipline.isDisciplineForStudentChoice) {
                    WordRowToInsert(
                        discipline.title,
                        semesterNumber.toString(),
                        discipline.creditHourQuantity.toInt().toString(),
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
            if (gradeControl.disciplines.size < 1) continue
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