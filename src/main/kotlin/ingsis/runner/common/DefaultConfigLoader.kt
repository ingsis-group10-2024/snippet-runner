package ingsis.runner.common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import config.ConfigLoader
import config.ConfigRule
import config.VerificationConfig
import ingsis.runner.runner.model.dto.RuleDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DefaultConfigLoader : ConfigLoader {
    private val objectMapper = jacksonObjectMapper()
    private val logger: Logger = LoggerFactory.getLogger(DefaultConfigLoader::class.java)

    override fun loadConfig(): VerificationConfig {
        val configFilePath = "StaticCodeAnalyzerRules.json"
        return objectMapper.readValue(
            this::class.java.classLoader.getResourceAsStream(configFilePath),
            VerificationConfig::class.java,
        )
    }

    fun loadConfigWithRules(rules: List<RuleDTO>): VerificationConfig {
        val configRules =
            if (rules.isNotEmpty()) {
                rules.map { ruleDto ->
                    mapToConfigRule(ruleDto)
                }
            } else {
                loadConfig().activeRules
            }
        return VerificationConfig(configRules)
    }

    // Method to map a list of RuleDto to a list of ConfigRule
    private fun mapToConfigRule(ruleDto: RuleDTO): ConfigRule {
        val valueInt = ruleDto.value?.toIntOrNull() ?: 0
        logger.info("Rule name: ${ruleDto.name}, value: ${ruleDto.value}, converted to: $valueInt")
        return ConfigRule(
            name = ruleDto.name,
            enabled = ruleDto.isActive,
            value = valueInt,
        )
    }
}
