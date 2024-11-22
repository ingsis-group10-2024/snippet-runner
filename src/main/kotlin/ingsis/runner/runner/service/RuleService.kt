package ingsis.runner.runner.service

import ingsis.runner.runner.exception.RuleNotFoundException
import ingsis.runner.runner.exception.UnauthorizedAccessException
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.enums.RuleTypeEnum
import ingsis.runner.runner.persistance.entity.Rule
import ingsis.runner.runner.persistance.repository.RuleRepository
import ingsis.runner.runner.redis.model.RuleChangeEvent
import ingsis.runner.runner.redis.producer.RedisRuleChangerProducer
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RuleService
    @Autowired
    constructor(
        private val ruleRepository: RuleRepository,
        private val ruleChangerProducer: RedisRuleChangerProducer,
    ) {
        private val logger: Logger = LoggerFactory.getLogger(RunnerService::class.java)

        fun getRules(
            userId: String,
            ruleType: RuleTypeEnum,
        ): List<RuleDTO> {
            // Get all rules for the user and the rule type
            logger.info("Getting rules for user: $userId and rule type: $ruleType")
            val rules =
                ruleRepository
                    .findByUserIdAndType(userId, ruleType)
                    .filter { it.isActive } // Filter out inactive rules
            logger.info("Found rules: $rules")
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
            logger.info("Creating or updating rules for user: $userId and rule type: $ruleType")
            val rulesToSave =
                newRules.map { dto ->
                    val existingRule =
                        if (dto.id != null) {
                            ruleRepository.findByUserIdAndNameAndType(userId = userId, name = dto.name, type = ruleType)
                        } else {
                            null
                        }

                    if (existingRule != null) {
                        // If rule exists, update it
                        logger.info("Rule already exists. Updating it with id: ${existingRule.id}")
                        existingRule.apply {
                            isActive = dto.isActive
                            value = dto.value
                        }
                        existingRule
                    } else {
                        // If rule does not exist, create a new one
                        logger.info("Rule does not exist. Creating a new one: $dto")
                        Rule(
                            userId = userId,
                            name = dto.name,
                            isActive = dto.isActive,
                            value = dto.value,
                            type = ruleType,
                        )
                    }
                }
            logger.info("Saving rules: $rulesToSave")
            val savedRules = ruleRepository.saveAll(rulesToSave).map { RuleDTO(it) }
            logger.info("Calling producer to publish rule change event")
            // Call the producer to publish the changed rules event
            mono {
                val ruleChangeEvent =
                    RuleChangeEvent(
                        ruleType = ruleType.name,
                        userId = userId,
                        timestamp = System.currentTimeMillis(),
                    )
                ruleChangerProducer.publishRuleChangeEvent(ruleChangeEvent)
            }.subscribe()

            return savedRules
        }

        fun deleteRule(
            userId: String,
            ruleId: String,
        ) {
            logger.info("Deleting rule with id: $ruleId")
            val rule =
                ruleRepository.findById(ruleId).orElse(null)
                    ?: throw RuleNotFoundException("Rule not found with id: $ruleId")

            if (rule.userId != userId) {
                logger.error("User does not have OWNER permission to delete this rule")
                throw UnauthorizedAccessException("User does not have permission to delete this rule")
            }
            ruleRepository.delete(rule)
        }
    }
