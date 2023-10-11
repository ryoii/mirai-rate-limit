package cn.ryoii.limiter

import net.mamoe.mirai.event.ConcurrencyKind
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotEvent
import kotlin.coroutines.EmptyCoroutineContext

enum class LimitBehavior {
    DROP,
    WAIT,
}

inline fun <reified T : BotEvent> EventChannel<*>.limitBotEvent(
    limiter: RateLimiter,
    behavior: LimitBehavior = LimitBehavior.DROP,
    crossinline filter: (T) -> Boolean = { true },
) = subscribeAlways<T>(
    coroutineContext = EmptyCoroutineContext,
    concurrency = ConcurrencyKind.CONCURRENT,
    priority = EventPriority.HIGHEST,
) {
    if (filter.invoke(it)) {
        if (behavior == LimitBehavior.DROP) {
            if (!limiter.tryAcquire()) {
                it.intercept()
            }
        } else if (behavior == LimitBehavior.WAIT) {
            limiter.acquire()
        }
    }
}