package ingsis.runner.runner.redis.config

import ingsis.runner.runner.redis.model.SnippetsValidationMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class ConnectionFactory(
    @Value("\${spring.data.redis.host}") private val hostName: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(
            RedisStandaloneConfiguration(hostName, port),
        )

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, SnippetsValidationMessage> {
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(SnippetsValidationMessage::class.java)

        val redisConnectionFactory: ReactiveRedisConnectionFactory = LettuceConnectionFactory()

        return ReactiveRedisTemplate(
            redisConnectionFactory,
            RedisSerializationContext.newSerializationContext<String, SnippetsValidationMessage>()
                .key(StringRedisSerializer())
                .value(jackson2JsonRedisSerializer) // Asegúrate de que esto coincida en el productor y consumidor
                .hashKey(StringRedisSerializer())
                .hashValue(jackson2JsonRedisSerializer)
                .build()
        )
    }

}
