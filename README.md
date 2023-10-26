# Mirai-Rate-Limit

使用令牌桶实现消息发送限流

## 配置文件
```yaml
# .\config\cn.ryoii.mirai-rate-limit\rate-limit.yml

# 全局限流
global:
  # 是否开启，默认开启
  enable: true
  # 限流行为：
  # drop: 丢弃, 停止本次消息发送
  # wait: 等待, 延时直到满足限流条件后发送
  behavior: drop
  # 暖机流量
  # 启动时可能会有大量消息发送，这里可以设置一个暖机流量，避免触发限流
  warmUp: 0
  # 流量突刺因子
  # 限流器会根据突刺因子计算出一个突刺流量，用于应对突发流量
  # 例如突刺因子为 0.5, TPS限制 100/min，则可应对最高 150/min 的突发流量
  burstFactor: 0.0
  # 流量限制为 limit/period
  # 例如 1 / 1s，即 TPS 为 1
  # 例如 1000 / 10s， 即 TPS 为 100
  limit: 1
  # 限流周期，格式参考 ISO-8601 Duration format
  # 或者如 17s，17m, 17h, 17d, 17m17s, 17d17h17m17s 的格式
  period: 1s

# Bot 限流
# bot: TODO

# 群聊限流
# group: TODO

# 私聊限流
# friend: TODO
```

## TODO

+ [ ] 支持按 Bot, 群聊， 私聊分别限流
+ [ ] 提供 console 指令动态调整限流
