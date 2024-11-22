package ingsis.runner.runner.service.common

import ast.ASTNode
import ingsis.runner.common.DefaultConfigLoader
import ingsis.runner.common.DefaultLexerConfig
import ingsis.runner.runner.exception.InvalidSnippetException
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.dto.lint.ValidationResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import parser.Parser
import sca.StaticCodeAnalyzer
import token.Token

@Service
class ParserService {
    private val configLoader = DefaultConfigLoader()
    private val lexerVersionController = DefaultLexerConfig()

    private val logger: Logger = LoggerFactory.getLogger(ParserService::class.java)

    fun validateSnippet(
        name: String,
        content: String,
        version: String,
        lintingRules: List<RuleDTO>,
    ): ValidationResponse {
        logger.info("Validating snippet with version: $version and content: $content. Linting rules: $lintingRules")
        val astNodes = parse(content, version)

        configLoader.loadConfigWithRules(lintingRules)

        val analyzer = StaticCodeAnalyzer(configLoader)
        val errors = analyzer.analyze(astNodes)

        if (errors.isNotEmpty()) {
            val errorMessages = errors.map { it.message }
            logger.error("Invalid snippet: $errorMessages")
            throw InvalidSnippetException(errorMessages)
        }
        logger.info("Snippet is valid.")
        return ValidationResponse(name, true, content, emptyList())
    }

    fun parse(
        content: String,
        version: String,
    ): List<ASTNode> {
        logger.info("Parsing snippet with version: $version and content: $content")
        val finalVersion = version.ifBlank { "1.1" }
        val inputStream = content.byteInputStream()
        val lexer = lexerVersionController.lexerVersionController().getLexer(finalVersion, inputStream)

        val tokens = mutableListOf<Token>()
        var token: Token? = lexer.getNextToken()
        while (token != null) {
            tokens.add(token)
            token = lexer.getNextToken()
        }
        logger.info("Tokens: $tokens")

        val parser = Parser(tokens)
        val astNodes = parser.generateAST()
        logger.info("AST Nodes: $astNodes")
        return astNodes
    }
}
