package cn.ryoii.limiter

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

/**
 * 令牌桶限流器实现
 *
 * 根据 `limitPerMinute` 计算出每个令牌生成的时间间隔，通过间隔计算出每个请求进入时的可用令牌数。
 *
 * @param overloadRate 并发控制，
 * @param limiterPerDuration 平均每个单位时间限流数
 * @param duration 单位时间
 */
open class RateLimiter(warmUp: Int, burstFactor: Double, tokenPerDuration: Int, duration: Duration) {
    private var stored: Int
    private var maxStored: Int
    private var tickInterval: Long
    private var nextTime: Long
    private val mutex = Mutex()

    init {
        check(warmUp >= 0) { "warmUp must be positive." }
        check(burstFactor >= 0) { "burstFactor must be positive." }
        check(tokenPerDuration > 0) { "tokenPerDuration must be positive." }
        check(duration.inWholeMilliseconds > 0) { "duration must be positive." }

        stored = warmUp
        maxStored = (tokenPerDuration * burstFactor).toInt()
        tickInterval = duration.div(tokenPerDuration).inWholeMilliseconds
        nextTime = now()
    }

    /**
     * 尝试获取令牌，不会导致请求挂起
     *
     * @param count 一次获取的令牌数，获取多个令牌说明该请求占用更多的资源，在同一个分组中占用更大的比重
     */
    suspend fun tryAcquire(count: Int = 1): Boolean {
        check(count > 0) { "acquire count must be positive." }
        mutex.withLock {
            val nowMillis = now()
            calToken(nowMillis)
            val waitMillis = nextTime - nowMillis

            return if (waitMillis <= 0) {
                consumeToken(count)
                true
            } else {
                false
            }
        }
    }

    /**
     * 尝试获取令牌，当没有令牌可用时，会挂起直到获取令牌成功
     *
     * @param count 一次获取的令牌数，获取多个令牌说明该请求占用更多的资源，在同一个分组中占用更大的比重
     */
    suspend fun acquire(count: Int = 1): Long {
        check(count > 0) { "acquire count must be positive." }
        mutex.withLock {
            val nowMillis = now()
            calToken(nowMillis)
            val waitMillis = max(nextTime - nowMillis, 0)
            consumeToken(count)

            if (waitMillis > 0) {
                delay(waitMillis)
            }

            return waitMillis
        }
    }

    private fun consumeToken(count: Int) {
        val consumed = min(count, this.stored)
        this.nextTime += (count - consumed) * tickInterval
        this.stored -= consumed
    }

    private fun calToken(nowMillis: Long) {
        if (nowMillis > nextTime) {
            stored = min(maxStored, stored + ((nowMillis - nextTime) / tickInterval).toInt())
            nextTime = nowMillis
        }
    }

    private fun now() = System.currentTimeMillis()
}
