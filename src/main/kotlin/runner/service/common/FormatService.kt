package runner.service.common

import ast.ASTNode
import implementation.Formatter
import org.springframework.stereotype.Service
import runner.model.dto.FormatResponse

@Service
class FormatService {
    fun format(astNodes: List<ASTNode>): FormatResponse {
        val formatter = Formatter("src/main/resources/FormatterRules.json")
        return FormatResponse(formatter.format(astNodes))
    }
}
