package language.service

import common.DefaultConfigLoader
import common.LexerConfig
import language.model.dto.ValidationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import parser.Parser
import sca.StaticCodeAnalyzer
import token.Token

@Service
class ParserService(
    @Autowired private val configLoader: DefaultConfigLoader,
    @Autowired private val lexerVersionController: LexerConfig,
) {
    fun validateSnippet(
        content: String,
        version: String,
    ): ValidationResponse {
        val inputStream = content.byteInputStream()
        val lexer = lexerVersionController.lexerVersionController().getLexer(version, inputStream)

        val tokens = mutableListOf<Token>()
        var token: Token? = lexer.getNextToken()
        while (token != null) {
            tokens.add(token)
            token = lexer.getNextToken()
        }
        println("Tokens: $tokens") // DEBUG

        val parser = Parser(tokens)
        val astNodes = parser.generateAST()
        println("AST Nodes: $astNodes") // DEBUG

        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(astNodes)
        val isValid = errors.isEmpty()
        val response = ValidationResponse(isValid, content, errors)
        return response
    }
}
