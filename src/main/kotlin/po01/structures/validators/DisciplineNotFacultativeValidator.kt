package po01.structures.validators

import jakarta.inject.Singleton

@Singleton
class DisciplineNotFacultativeValidator {
    private val invalidGradesRegexp = """^(Факультатив: )""".toRegex()

    fun validate(gradeResult: String): Boolean {
        val matchResult = invalidGradesRegexp.find(gradeResult)
        return matchResult?.let {
            false
        } ?: true
    }
}