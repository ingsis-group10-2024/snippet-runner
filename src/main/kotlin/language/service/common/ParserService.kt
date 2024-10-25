package language.service.common

import ast.ASTNode
import common.DefaultConfigLoader
import common.DefaultLexerConfig
import language.exception.InvalidSnippetException
import language.model.dto.ValidationResponse
import org.springframework.stereotype.Service
import parser.Parser
import sca.StaticCodeAnalyzer
import token.Token

@Service
class ParserService {
    private val configLoader = DefaultConfigLoader()
    private val lexerVersionController = DefaultLexerConfig()

    fun validateSnippet(
        content: String,
        version: String,
    ): ValidationResponse {
        val astNodes = parse(content, version)
        println("AST Nodes: $astNodes") // DEBUG

        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(astNodes)

        if (errors.isNotEmpty()) {
            // Extraer los mensajes de error
            val errorMessages = errors.map { it.message } // Usar directamente el campo message
            throw InvalidSnippetException(errorMessages) // Lanza la excepción con los mensajes
        }

        return ValidationResponse(true, content, emptyList())
    }

    fun parse(
        content: String,
        version: String,
    ): List<ASTNode> {
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
        return astNodes
    }
}
