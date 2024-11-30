package ingsis.runner.runner.service.common

import ast.ASTNode
import com.nimbusds.jose.shaded.gson.Gson
import com.nimbusds.jose.shaded.gson.JsonObject
import implementation.Formatter
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.dto.format.FormatResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Files

@Service
class FormatService {

    private val logger: Logger = LoggerFactory.getLogger(FormatService::class.java)

    fun format(astNodes: List<ASTNode>, rules: List<RuleDTO>): FormatResponse {
        logger.info("Formatting code with rules: $rules")
        val configFilePath = createTempConfigFile(rules)
        val formatter = Formatter(configFilePath)
        val formattedCode = formatter.format(astNodes)
        logger.info("Code formatted successfully.")
        return FormatResponse(formattedCode)
    }
    private fun createTempConfigFile(rules: List<RuleDTO>): String {
        val tempFile = Files.createTempFile("FormatterRules", ".json").toFile()
        val jsonContent = buildJsonContent(rules)
        logger.info("Creating temporary config file with content: $jsonContent")
        tempFile.writeText(jsonContent)
        // Delete file on exit
        tempFile.deleteOnExit()
        return tempFile.absolutePath
    }
    private fun buildJsonContent(rules: List<RuleDTO>): String {
        val jsonObject = JsonObject()

        // Default values
        val spaceBeforeColon = rules.find { it.name == "spaceBeforeColon" && it.isActive }?.value?.toInt() ?: 1
        val spaceAfterColon = rules.find { it.name == "spaceAfterColon" && it.isActive }?.value?.toInt() ?: 1
        val spaceBeforeAndAfterAssignationOperator = rules.find { it.name == "spaceBeforeAndAfterAssignationOperator" && it.isActive }?.value?.toInt() ?: 2
        val newlinesBeforePrintln = rules.find { it.name == "newlinesBeforePrintln" && it.isActive }?.value?.toInt() ?: 3

        // Add values to JSON object
        jsonObject.addProperty("spaceBeforeColon", spaceBeforeColon)
        jsonObject.addProperty("spaceAfterColon", spaceAfterColon)
        jsonObject.addProperty("spaceBeforeAndAfterAssignationOperator", spaceBeforeAndAfterAssignationOperator)
        jsonObject.addProperty("newlinesBeforePrintln", newlinesBeforePrintln)

        // Convert JSON object to string
        return Gson().toJson(jsonObject)
    }

}