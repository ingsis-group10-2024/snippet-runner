package ingsis.runner.runner.service.common

import ast.ASTNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import config.CustomizableFormatterRules
import config.VerificationConfig
import implementation.Formatter
import ingsis.runner.common.DefaultConfigLoader
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.dto.format.FormatResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Service
class FormatService {
    private val configLoader = DefaultConfigLoader()

    private val logger: Logger = LoggerFactory.getLogger(FormatService::class.java)

    fun format(
        astNodes: List<ASTNode>,
        rules: List<RuleDTO>,
    ): FormatResponse {
        // Check for custom rules. If they exist then create a temporary file with them
        val configFilePath =
            if (rules.isNotEmpty()) {
                logger.info("Custom rules found. Creating temporary config file with: $rules")
                val verificationConfig = configLoader.loadConfigWithRules(rules)
                logger.info("Verification config created: $verificationConfig")
                val tempFile = createTempConfigFile(verificationConfig)
                logger.info("Temporary config file created: $tempFile with verification config")
                tempFile.toString()
            } else {
                // Load the default formatter rules from resources
                logger.info("No custom rules found. Loading default formatter rules.")
                val inputStream: InputStream =
                    this::class.java.classLoader
                        .getResourceAsStream("FormatterRules.json")
                        ?: throw Exception("FormatterRules.json file not found in resources.")

                // If necessary, save the inputStream content to a temporary file
                val tempFile = Files.createTempFile("formatterRules", ".json").toFile()
                logger.info("Saving formatter rules to temporary file: $tempFile")
                inputStream.copyTo(tempFile.outputStream())
                logger.info("Temporary config file created at: $tempFile")
                tempFile.toString() // Return the temporary file path
            }

        // Create the formatter
        logger.info("Creating formatter with config file: $configFilePath")
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

        logger.info("Custom formatter rules created: $customFormatterRules")

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

        logger.info("Custom formatter rules mapped to JSON: $jsonConfig")

        // Write the JSON string to the temporary file
        Files.write(tempFile.toPath(), jsonConfig.toByteArray(), StandardOpenOption.WRITE)

        return tempFile
    }
}
