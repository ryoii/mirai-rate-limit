package cn.ryoii.limiter

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.seconds

class LimiterTest {

    @Test
    fun test1QPS() {
        val limiter = RateLimiter(0, .0, 1, 1.seconds)
        val count = AtomicInteger(0)

        runBlocking {
            assertFails {
                withTimeout(3.seconds) {
                    for (i in 0..100) {
                        launch {
                            limiter.acquire()
                            count.incrementAndGet()
                        }
                    }
                }
            }
        }

        count.get().assertEqualsEnough(3)
    }

    @Test
    fun test5QPS() {
        val limiter = RateLimiter(0, .0, 5, 1.seconds)
        val count = AtomicInteger(0)

        runBlocking {
            assertFails {
                withTimeout(3.seconds) {
                    for (i in 0..100) {
                        launch {
                            limiter.acquire()
                            count.incrementAndGet()
                        }
                    }
                }
            }
        }

        count.get().assertEqualsEnough(15)
    }

    @Test
    fun test5QPSWithMaxBurst() {
        val limiter = RateLimiter(5, .0, 5, 1.seconds)
        val count = AtomicInteger(0)

        runBlocking {
            delay(2000)

            assertFails {
                withTimeout(3.seconds) {
                    for (i in 0..100) {
                        launch {
                            limiter.acquire()
                            count.incrementAndGet()
                        }
                    }
                }
            }
        }

        count.get().assertEqualsEnough(20)
    }

    private fun Int.assertEqualsEnough(expect: Int, enough: Int = 1) {
        assert(this in expect - enough..expect + enough) { "value $this not in range [${expect - enough}, ${expect + enough}]" }
    }
}