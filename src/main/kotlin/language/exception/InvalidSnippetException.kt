package language.exception

class InvalidSnippetException(val errors: List<String>) : RuntimeException("Invalid snippet")