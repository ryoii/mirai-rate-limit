package cn.ryoii.limiter

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessagePreSendEvent
import kotlin.time.Duration

object RateLimitPlugin : KotlinPlugin(JvmPluginDescription.loadFromResource()) {

    private val config = RateLimitConfig()
    private var globalListener: Listener<MessagePreSendEvent>? = null

    override fun onEnable() {
        config.reload()

        with(config.global) {
            if (!enable) return@with
            logger.info("global rate limit enabled: limit rate $limit per $duration")
            val limiter = RateLimiter(warmUp, burstFactor, limit, Duration.parse(duration))
            globalListener = GlobalEventChannel.limitBotEvent<MessagePreSendEvent>(limiter, behavior = getLimitBehavior(behavior))
                .also {
                    it.invokeOnCompletion {
                        logger.info("global rate limit complete")
                    }
                }
        }
    }

    override fun onDisable() {
        globalListener?.complete()
        globalListener = null
    }
}

fun getLimitBehavior(behavior: String): LimitBehavior {
    return when (behavior) {
        "drop" -> LimitBehavior.DROP
        "wait" -> LimitBehavior.WAIT
        else -> throw IllegalArgumentException("unknown limit behavior: $behavior")
    }
}