package cn.ryoii.limiter

import net.mamoe.mirai.event.ConcurrencyKind
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.BotEvent
import kotlin.coroutines.EmptyCoroutineContext

inline fun <reified T: BotEvent> EventChannel<BotEvent>.limit() {
    subscribeAlways<T> (
        coroutineContext = EmptyCoroutineContext,
        concurrency = ConcurrencyKind.CONCURRENT,
        priority = EventPriority.HIGHEST,
    ) {
        TODO()
    }
}