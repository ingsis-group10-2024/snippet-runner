package common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import config.ConfigLoader
import config.VerificationConfig

class DefaultConfigLoader : ConfigLoader {
    private val objectMapper = jacksonObjectMapper()

    override fun loadConfig(): VerificationConfig {
        val configFilePath = "StaticCodeAnalyzerRules.json"
        return objectMapper.readValue(
            this::class.java.classLoader.getResourceAsStream(configFilePath),
            VerificationConfig::class.java,
        )
    }
}
