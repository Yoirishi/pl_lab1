package po01.structures

import jakarta.inject.Singleton

@Singleton
class DifferentialTestChecker {
    val regexp = """^(отл.*|хор.*|уд.*)""".toRegex()


    /**
     * если оценка Н/Я то как определить тип итоговой отчетности? Неизвестно
     */
    fun checkIsTestDifferential(gradeResult: String): Boolean {
        val matchResult = regexp.find(gradeResult)
        return matchResult?.let {
            true
        } ?: false
    }
}