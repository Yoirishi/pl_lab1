package po01.excelparser.enums

enum class TargetCourse {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH,
    SEVENTH,
    EIGHTH;

    companion object {
        fun getListForIntegerValue(value: UInt): List<TargetCourse> {
            if (value > 8u) throw IllegalArgumentException()
            return when (value) {
                8u -> {
                    TargetCourse.values().toList()
                }
                1u -> {
                    listOf(
                        FIRST
                    )
                }
                2u -> {
                    listOf(
                        FIRST,
                        SECOND
                    )
                }
                3u -> {
                    listOf(
                        FIRST,
                        SECOND,
                        THIRD
                    )
                }
                4u -> {
                    listOf(
                        FIRST,
                        SECOND,
                        THIRD,
                        FOURTH
                    )
                }
                5u -> {
                    listOf(
                        FIRST,
                        SECOND,
                        THIRD,
                        FOURTH,
                        FIFTH
                    )
                }
                6u -> {
                    listOf(
                        FIRST,
                        SECOND,
                        THIRD,
                        FOURTH,
                        FIFTH,
                        SIXTH
                    )
                }
                7u -> {
                    listOf(
                        FIRST,
                        SECOND,
                        THIRD,
                        FOURTH,
                        FIFTH,
                        SIXTH,
                        SEVENTH
                    )
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
