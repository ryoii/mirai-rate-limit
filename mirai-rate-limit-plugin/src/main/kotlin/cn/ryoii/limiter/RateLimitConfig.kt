package cn.ryoii.limiter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

class RateLimitConfig : AutoSavePluginConfig("rate-limit") {
    val global: LimiterConfig by value(LimiterConfig())
    val group: Map<Int, LimiterConfig> by value(emptyMap())
    val friend: Map<Long, LimiterConfig> by value(emptyMap())
}

@Serializable
class LimiterConfig {
    val enable: Boolean = true
    val behavior: String = "drop"
    val warmUp: Int = 0
    val burstFactor: Double = 0.0
    val limit: Int = 1
    val duration: String = "1s"
}