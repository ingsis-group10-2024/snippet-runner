package runner.service.common

import ast.ASTNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import common.DefaultConfigLoader
import config.CustomizableFormatterRules
import config.VerificationConfig
import implementation.Formatter
import org.springframework.stereotype.Service
import runner.model.dto.FormatResponse
import runner.model.dto.RuleDto
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Service
class FormatService {

    private val configLoader = DefaultConfigLoader()

    fun format(astNodes: List<ASTNode>, rules: List<RuleDto>): FormatResponse {
        // Check for custom rules
        val configFilePath = if (rules.isNotEmpty()) {
            // If there are custom rules, load the configuration with them
            val verificationConfig = configLoader.loadConfigWithRules(rules)
            val tempFile = createTempConfigFile(verificationConfig)
            tempFile.toString()  // Return the path to the temporary file
        } else {
            // If there are no custom rules, use the default configuration
            "src/main/resources/FormatterRules.json"
        }

        // Create the formatter
        val formatter = Formatter(configFilePath)

        return FormatResponse(formatter.format(astNodes))
    }

    // Method to create a temporary file with the custom rules
    private fun createTempConfigFile(config: VerificationConfig): File {
        val tempFile = Files.createTempFile("formatterConfig", ".json").toFile()

        // Convert the custom rules to JSON format
        val objectMapper = jacksonObjectMapper()
        val customFormatterRules = CustomizableFormatterRules(
            spaceBeforeColon = config.activeRules.find { it.name == "spaceBeforeColon" }?.value ?: 1,
            spaceAfterColon = config.activeRules.find { it.name == "spaceAfterColon" }?.value ?: 1,
            spaceBeforeAndAfterAssignationOperator = config.activeRules.find { it.name == "spaceBeforeAndAfterAssignationOperator" }?.value ?: 1,
            newlinesBeforePrintln = config.activeRules.find { it.name == "newlinesBeforePrintln" }?.value ?: 1
        )

        // Map the custom rules to a JSON string
        val jsonConfig = objectMapper.writeValueAsString(
            mapOf("newlinesAfterSemicolon" to 1, // default value
                "spacesBetweenTokens" to 1, // default value
                "spacesBeforeAndAfterOperators" to 1, // default value
                "custom" to customFormatterRules)
        )

        // Write the JSON string to the temporary file
        Files.write(tempFile.toPath(), jsonConfig.toByteArray(), StandardOpenOption.WRITE)

        return tempFile
    }
}
