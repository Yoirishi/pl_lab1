package po01.structures.validators

import jakarta.inject.Singleton

@Singleton
class CourseWorkIsProjectValidator {
    private val projectRegexp = """проект""".toRegex()

    fun validate(gradeForm: String): Boolean {
        val matchResult = projectRegexp.find(gradeForm)
        return matchResult?.let { true } ?: false
    }
}