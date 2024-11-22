package ingsis.runner.runner.redis.producer

import ingsis.runner.runner.redis.model.RuleChangeEvent
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisRuleChangerProducer
    @Autowired
    constructor(
        @Value("\${stream.key.rules-changed-channel}") streamKey: String,
        redis: ReactiveRedisTemplate<String, String>,
    ) : RedisStreamProducer(streamKey, redis),
        RuleChangerProducer {
        override suspend fun publishRuleChangeEvent(ruleChangeEvent: RuleChangeEvent) {
            println("Publishing rule change event to stream: $streamKey")

            emit(ruleChangeEvent).awaitSingle()
        }
    }
