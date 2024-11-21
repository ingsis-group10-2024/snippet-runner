package runner.redis.producer

import runner.redis.model.RuleChangeEvent

interface RuleChangerProducer {
    suspend fun publishRuleChangeEvent(ruleChangeEvent: RuleChangeEvent)
}
