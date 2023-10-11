package cn.ryoii.limiter

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessagePreSendEvent
import net.mamoe.mirai.mock.MockBotFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class RateLimitTest {

    @Test
    fun testLimitDrop() {
        val limiter = RateLimiter(0, .0, 1, 1.seconds)
        val count = AtomicInteger(0)

        GlobalEventChannel.limitBotEvent<MessagePreSendEvent>(limiter, behavior = LimitBehavior.DROP)
        val bot = MockBotFactory.newMockBotBuilder()
            .id(1)
            .nick("bot")
            .create()

        val friend = bot.addFriend(2, "friend")

        GlobalEventChannel.subscribeAlways<MessagePreSendEvent> {
            count.incrementAndGet()
        }

        runBlocking {
            val job = launch {
                for (i in 0..100) {
                    delay(100)
                    friend.sendMessage("hello")
                }
            }
            delay(3.seconds)
            job.cancel()
        }

        count.get().assertEqualsEnough(3)
    }

    @Test
    fun testLimitWait() {
        val limiter = RateLimiter(0, .0, 1, 1.seconds)
        val count = AtomicInteger(0)

        GlobalEventChannel.limitBotEvent<MessagePreSendEvent>(limiter, behavior = LimitBehavior.WAIT)
        val bot = MockBotFactory.newMockBotBuilder()
            .id(1)
            .nick("bot")
            .create()

        val friend = bot.addFriend(2, "friend")

        GlobalEventChannel.subscribeAlways<MessagePreSendEvent> {
            count.incrementAndGet()
        }

        runBlocking {
            val job = launch {
                runCatching {
                    for (i in 0..100) {
                        delay(100)
                        friend.sendMessage("hello")
                    }
                }
            }
            delay(3.seconds)
            job.cancel()
        }

        count.get().assertEqualsEnough(3)
    }

    private fun Int.assertEqualsEnough(expect: Int, enough: Int = 1) {
        assert(this in expect - enough..expect + enough) { "value $this not in range [${expect - enough}, ${expect + enough}]" }
    }
}