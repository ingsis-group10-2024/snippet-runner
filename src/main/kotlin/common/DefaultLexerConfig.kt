package common

import controller.LexerVersionController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DefaultLexerConfig {
    @Bean
    fun lexerVersionController(): LexerVersionController = LexerVersionController()
}
