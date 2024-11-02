package runner.service.common

import ast.ASTNode
import common.DefaultConfigLoader
import common.DefaultLexerConfig
import org.springframework.stereotype.Service
import parser.Parser
import runner.exception.InvalidSnippetException
import runner.model.dto.ValidationResponse
import sca.StaticCodeAnalyzer
import token.Token

@Service
class ParserService {
    private val configLoader = DefaultConfigLoader()
    private val lexerVersionController = DefaultLexerConfig()

    fun validateSnippet(
        name: String,
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

        return ValidationResponse(name,true, content, emptyList())
    }

    fun parse(
        content: String,
        version: String,
    ): List<ASTNode> {
        val finalVersion = version.ifBlank { "1.1" }
        val inputStream = content.byteInputStream()
        val lexer = lexerVersionController.lexerVersionController().getLexer(finalVersion, inputStream)

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
