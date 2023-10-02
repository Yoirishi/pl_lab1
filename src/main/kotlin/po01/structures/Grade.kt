package po01.structures

import po01.excelparser.enums.GradeType

sealed class Grade(
    val gradeType: GradeType
)

class ExamGrade(
    val gradeResult: String
): Grade(GradeType.EXAM)

class TestGrade(
    val gradeResult: String
): Grade(GradeType.TEST)

class DifferentialTestGrade(
    val gradeResult: String
): Grade(GradeType.DIFFERENTIAL_TEST)

class ExamWithCWGrades(
    val gradeResult: String,
    val cwGradeResult: String
): Grade(GradeType.EXAM)

class TestWithCWGrades(
    val gradeResult: String,
    val cwGradeResult: String
): Grade(GradeType.TEST)

class DifferentialTestWithCWGrades(
    val gradeResult: String,
    val cwGradeResult: String
): Grade(GradeType.DIFFERENTIAL_TEST)

