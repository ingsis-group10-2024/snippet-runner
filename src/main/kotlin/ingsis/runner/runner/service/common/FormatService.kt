package ingsis.runner.runner.service.common

import ast.ASTNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import config.CustomizableFormatterRules
import config.VerificationConfig
import implementation.Formatter
import ingsis.runner.common.DefaultConfigLoader
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.dto.format.FormatResponse
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Service
class FormatService {
    private val configLoader = DefaultConfigLoader()

    fun format(
        astNodes: List<ASTNode>,
        rules: List<RuleDTO>,
    ): FormatResponse {
        // Check for custom rule. If they exist then create a temporary file with them
        val configFilePath =
            if (rules.isNotEmpty()) {
                val verificationConfig = configLoader.loadConfigWithRules(rules)
                val tempFile = createTempConfigFile(verificationConfig)
                tempFile.toString()
            } else {
                // Load the default formatter rules from resources
                val inputStream: InputStream =
                    this::class.java.classLoader
                        .getResourceAsStream("FormatterRules.json")
                        ?: throw Exception("FormatterRules.json file not found in resources.")

                // If necessary, save the inputStream content to a temporary file
                val tempFile = Files.createTempFile("formatterRules", ".json").toFile()
                inputStream.copyTo(tempFile.outputStream())
                tempFile.toString() // Return the temporary file path
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
        val customFormatterRules =
            CustomizableFormatterRules(
                spaceBeforeColon = config.activeRules.find { it.name == "spaceBeforeColon" }?.value ?: 1,
                spaceAfterColon = config.activeRules.find { it.name == "spaceAfterColon" }?.value ?: 1,
                spaceBeforeAndAfterAssignationOperator =
                    config.activeRules.find { it.name == "spaceBeforeAndAfterAssignationOperator" }?.value ?: 1,
                newlinesBeforePrintln = config.activeRules.find { it.name == "newlinesBeforePrintln" }?.value ?: 1,
            )

        // Map the custom rules to a JSON string
        val jsonConfig =
            objectMapper.writeValueAsString(
                mapOf(
                    "newlinesAfterSemicolon" to 1, // default value
                    "spacesBetweenTokens" to 1, // default value
                    "spacesBeforeAndAfterOperators" to 1, // default value
                    "custom" to customFormatterRules,
                ),
            )

        // Write the JSON string to the temporary file
        Files.write(tempFile.toPath(), jsonConfig.toByteArray(), StandardOpenOption.WRITE)

        return tempFile
    }
}
