package common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import config.ConfigLoader
import config.ConfigRule
import config.VerificationConfig
import runner.model.dto.RuleDto

class DefaultConfigLoader : ConfigLoader {
    private val objectMapper = jacksonObjectMapper()

    override fun loadConfig(): VerificationConfig {
        val configFilePath = "StaticCodeAnalyzerRules.json"
        return objectMapper.readValue(
            this::class.java.classLoader.getResourceAsStream(configFilePath),
            VerificationConfig::class.java,
        )
    }

    fun loadConfigWithRules(rules: List<RuleDto>): VerificationConfig {
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
    private fun mapToConfigRule(ruleDto: RuleDto): ConfigRule =
        ConfigRule(
            name = ruleDto.name,
            enabled = ruleDto.isActive,
            value = ruleDto.value?.toIntOrNull() ?: 0, // Convert value to Int or 0 if null
        )
}
