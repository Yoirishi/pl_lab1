package po01.structures.validators

import jakarta.inject.Singleton

@Singleton
class CreditHoursValidator {
    fun validate(creditHours: String): Boolean {
        val creditHoursAsDouble = creditHours.toDouble()
        return (creditHoursAsDouble.toInt().toDouble() == creditHoursAsDouble)
    }
}