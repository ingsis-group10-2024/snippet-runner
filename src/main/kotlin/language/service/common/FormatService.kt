package language.service.common

import ast.ASTNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import implementation.Formatter
import language.model.dto.FormatResponse
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.nio.file.Files

@Service
class FormatService {

    fun format(astNodes: List<ASTNode>): FormatResponse {
        val formatter = Formatter("src/main/resources/FormatterRules.json")
        return FormatResponse(formatter.format(astNodes))
    }
}