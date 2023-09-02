package po01.structures.validators

import jakarta.inject.Singleton

@Singleton
class GradeResultValidator {
    private val validGradesRegexp = """^(хор.*|удо.*|отл.*|зач)""".toRegex()

    fun validate(gradeResult: String): Boolean {
        val matchResult = validGradesRegexp.find(gradeResult)
        return matchResult?.let {
            true
        } ?: false
    }
}