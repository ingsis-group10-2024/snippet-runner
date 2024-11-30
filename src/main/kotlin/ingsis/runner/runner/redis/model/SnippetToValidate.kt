package ingsis.runner.runner.redis.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SnippetToValidate @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("authorId") val authorId: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("language") val language: String,
    @JsonProperty("languageVersion") val languageVersion: String,
    @JsonProperty("extension") var extension: String,
    @JsonProperty("ruleType") val ruleType: String,
    @JsonProperty("authorizationHeader") val authorizationHeader: String
)