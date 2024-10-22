package language

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnippetLanguageApplication

fun main(args: Array<String>) {
    runApplication<SnippetLanguageApplication>(*args)
}
