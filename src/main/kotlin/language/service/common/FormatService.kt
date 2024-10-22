package language.service.common

import ast.ASTNode
import implementation.Formatter
import language.model.dto.FormatResponse
import org.springframework.stereotype.Service

@Service
class FormatService {

    fun format(astNodes: List<ASTNode>): FormatResponse {
        val formatter = Formatter("FormatterRules.json")
        return FormatResponse(formatter.format(astNodes))
    }
}