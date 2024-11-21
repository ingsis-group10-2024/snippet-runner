package runner.redis.producer

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import runner.redis.model.RuleChangeEvent

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
