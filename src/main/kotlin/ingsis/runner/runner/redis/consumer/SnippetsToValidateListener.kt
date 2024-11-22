package ingsis.runner.runner.redis.consumer

import ingsis.runner.runner.redis.model.SnippetsValidationMessage
import ingsis.runner.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("!test")
class SnippetsToValidateListener(
    @Value("\${stream.key.snippet-validation-channel}") streamKey: String,
    @Value("\${groups.rules}") groupId: String,
    redisTemplate: RedisTemplate<String, String>,
    private val runnerService: RunnerService,
) : RedisStreamConsumer<SnippetsValidationMessage>(streamKey, groupId, redisTemplate) {
    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, SnippetsValidationMessage>> =
        StreamReceiver.StreamReceiverOptions
            .builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(SnippetsValidationMessage::class.java)
            .build()

    override fun onMessage(record: ObjectRecord<String, SnippetsValidationMessage>) {
        val validationMessage = record.value
        println("Received validation message: ${validationMessage.ruleType} with ${validationMessage.snippets.size} snippets")
        // Process the snippets to validate them
        runnerService.validateOrFormatSnippets(validationMessage)
    }
}
