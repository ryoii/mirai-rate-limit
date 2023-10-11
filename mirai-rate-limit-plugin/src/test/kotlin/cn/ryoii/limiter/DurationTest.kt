package cn.ryoii.limiter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationTest {

    @Test
    fun testDurationParse() {
        assertEquals(17.seconds, Duration.parse("17s"))
        assertEquals(17.minutes, Duration.parse("17m"))
        assertEquals(17.hours, Duration.parse("17h"))
        assertEquals(17.days, Duration.parse("17d"))

        assertEquals(17.days + 17.hours + 17.minutes + 17.seconds, Duration.parse("17d17h17m17s"))
        assertEquals(1.days + 2.hours + 3.minutes + 4.seconds + 58.milliseconds, Duration.parse("P1DT2H3M4.058S"))
    }
}