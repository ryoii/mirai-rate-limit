package cn.ryoii.limiter

import net.mamoe.mirai.console.data.AutoSavePluginConfig

class RateLimitConfig : AutoSavePluginConfig("rate-limit") {
    val global: LimiterConfig = LimiterConfig()
    val group: Map<Int, LimiterConfig> = emptyMap()
    val friend: Map<Long, LimiterConfig> = emptyMap()
}

class LimiterConfig {
    val enable: Boolean = true
    val behavior: String = "drop"
    val warmUp: Int = 0
    val burstFactor: Double = 0.0
    val limit: Int = 0
    val duration: String = "second"
}