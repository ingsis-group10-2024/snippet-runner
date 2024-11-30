package ingsis.runner.runner.redis.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import ingsis.runner.runner.redis.model.SnippetToValidate
import ingsis.runner.runner.service.RunnerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
) : RedisStreamConsumer<String>(streamKey, groupId, redisTemplate) {
    private val logger: Logger = LoggerFactory.getLogger(SnippetsToValidateListener::class.java)

    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> =
        StreamReceiver.StreamReceiverOptions
            .builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(String::class.java)
            .build()

    override fun onMessage(record: ObjectRecord<String, String>) {
        val snippetJson = record.value
        logger.info("Received snippet validation message from Redis stream: $snippetJson")

        try {
            // Deserialize the Snippet
            val snippet = ObjectMapper().readValue(snippetJson, SnippetToValidate::class.java)
            logger.info("Deserialized snippet: $snippet")

            // Call the runner service to validate the snippet
            runnerService.validateOrFormatSnippet(snippet)
            logger.info("Snippet processed successfully: ${snippet.id}")
        } catch (ex: Exception) {
            logger.error("Failed to process message: ${record.value}", ex)
        }
    }
}
