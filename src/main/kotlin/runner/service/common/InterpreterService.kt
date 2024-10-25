package runner.service.common

import InterpreterFactory
import ast.ASTNode
import common.DefaultPrintEmitter
import org.springframework.stereotype.Service
import reader.ConsoleInputReader
import runner.model.dto.ExecutionResponse
import variable.VariableMap

@Service
class InterpreterService {
    fun execute(
        astNodes: List<ASTNode>,
        version: String,
    ): ExecutionResponse {
        val consoleInputReader = ConsoleInputReader()
        val interpreter = InterpreterFactory(version, VariableMap(HashMap()), consoleInputReader).buildInterpreter()

        val output = mutableListOf<String>() // List to capture the output
        val errors = mutableListOf<String>() // List to capture the errors

        try {
            val interpretedList = interpreter.interpret(astNodes)
            for (interpreted in interpretedList.second) {
                val result = DefaultPrintEmitter().emit(interpreted)
                output.add(result)
            }
        } catch (e: Exception) {
            errors.add(e.message.toString() ?: "An error occurred")
        }
        return ExecutionResponse(output, errors)
    }
}
