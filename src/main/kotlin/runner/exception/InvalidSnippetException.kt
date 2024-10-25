package runner.exception

class InvalidSnippetException(
    val errors: List<String>,
) : RuntimeException("Invalid snippet")
