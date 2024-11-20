package runner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.exception.RuleNotFoundException
import runner.exception.UnauthorizedAccessException
import runner.model.dto.RuleDTO
import runner.model.enums.RuleTypeEnum
import runner.persistance.entity.Rule
import runner.persistance.repository.RuleRepository

@Service
class RuleService
@Autowired
constructor(
    private val ruleRepository: RuleRepository,
) {
    fun getRules(userId: String, ruleType: RuleTypeEnum): List<RuleDTO> {
        // Get all rules for the user and the rule type
        val rules = ruleRepository.findByUserIdAndType(userId, ruleType)
            .filter { it.isActive }  // Filter out inactive rules

        // Convert the rules to DTOs
        return rules.map { rule ->
            RuleDTO(rule.id, rule.name, rule.isActive, rule.value)
        }
    }

    fun createOrUpdateRules(
        newRules: List<RuleDTO>,
        ruleType: RuleTypeEnum,
        userId: String,
    ): List<RuleDTO> {
        val rulesToSave = newRules.map { dto ->
            val existingRule = if (dto.id != null) {
                ruleRepository.findByUserIdAndNameAndType(userId = userId, name = dto.name, type = ruleType)
            } else {
                null
            }

            if (existingRule != null) {
                // If rule exists, update it
                existingRule.apply {
                    isActive = dto.isActive
                    value = dto.value
                }
                existingRule
            } else {
                // If rule does not exist, create a new one
                Rule(
                    userId = userId,
                    name = dto.name,
                    isActive = dto.isActive,
                    value = dto.value,
                    type = ruleType,
                )
            }
        }

        return ruleRepository.saveAll(rulesToSave).map { RuleDTO(it) }
    }

    fun deleteRule(
        userId: String,
        ruleId: String,
    ) {
        val rule =
            ruleRepository.findById(ruleId).orElse(null)
                ?: throw RuleNotFoundException("Rule not found with id: $ruleId")

        if (rule.userId != userId) {
            throw UnauthorizedAccessException("User does not have permission to delete this rule")
        }
        ruleRepository.delete(rule)
    }

}