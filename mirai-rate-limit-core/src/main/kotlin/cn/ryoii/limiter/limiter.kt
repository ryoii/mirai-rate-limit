package cn.ryoii.limiter

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

/**
 * 令牌桶限流器实现
 *
 * 根据 `limitPerMinute` 计算出每个令牌生成的时间间隔，通过间隔计算出每个请求进入时的可用令牌数。
 *
 * @param maxCache 最大令牌缓存数
 * @param limiterPerDuration 平均每个单位时间限流数
 * @param duration 单位时间
 */
open class RateLimiter(maxCache: Int, limiterPerDuration: Int, duration: Duration) {
    private var stored: Int
    private var maxStored: Int
    private var interval: Long
    private var nextTime: Long
    private val mutex = Mutex()

    init {
        stored = 0
        maxStored = maxCache
        interval = duration.div(limiterPerDuration).inWholeMilliseconds
        nextTime = now()
    }

    suspend fun setMaxCache(maxCache: Int) {
        this.mutex.lock()
        this.maxStored = maxCache
        this.mutex.unlock()
    }

    suspend fun setRate(limiterPerDuration: Int, duration: Duration) {
        this.mutex.lock()
        this.interval = duration.div(limiterPerDuration).inWholeMilliseconds
        this.mutex.unlock()
    }

    /**
     * 尝试获取令牌，不会导致请求挂起
     *
     * @param count 一次获取的令牌数，获取多个令牌说明该请求占用更多的资源，在同一个分组中占用更大的比重
     */
    suspend fun tryAcquire(count: Int): Boolean {
        check(count > 0) { "acquire count must be positive." }
        this.mutex.lock()
        val nowMillis = now()
        reSync(nowMillis)
        val waitMillis = nextTime - nowMillis

        return if (waitMillis <= 0) {
            updateNextTime(count)
            this.mutex.unlock()
            true
        } else {
            this.mutex.unlock()
            false
        }
    }

    /**
     * 尝试获取令牌，当没有令牌可用时，会挂起直到获取令牌成功
     *
     * @param count 一次获取的令牌数，获取多个令牌说明该请求占用更多的资源，在同一个分组中占用更大的比重
     */
    suspend fun acquire(count: Int): Long {
        check(count > 0) { "acquire count must be positive." }
        this.mutex.lock()

        val nowMillis = now()
        reSync(nowMillis)
        val waitMillis = max(nextTime - nowMillis, 0)
        updateNextTime(count)

        this.mutex.unlock()

        if (waitMillis > 0) {
            delay(waitMillis)
        }

        return waitMillis
    }

    private fun updateNextTime(count: Int) {
        val consumed = min(count, this.stored)
        val subsist = count - consumed

        val waitTime = subsist * interval
        this.nextTime += waitTime

        this.stored -= consumed
    }

    private fun reSync(nowMillis: Long) {
        if (nowMillis > nextTime) {
            stored = min(maxStored, stored + ((nowMillis - nextTime) / interval).toInt())
            nextTime = nowMillis
        }
    }

    private fun now() = System.currentTimeMillis()
}
