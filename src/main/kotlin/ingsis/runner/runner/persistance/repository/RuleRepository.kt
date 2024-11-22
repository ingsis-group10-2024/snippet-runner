package ingsis.runner.runner.persistance.repository

import ingsis.runner.runner.model.enums.RuleTypeEnum
import ingsis.runner.runner.persistance.entity.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, String> {
    fun findByUserIdAndType(
        userId: String,
        type: RuleTypeEnum,
    ): List<Rule>

    fun findByUserIdAndNameAndType(
        userId: String,
        name: String,
        type: RuleTypeEnum,
    ): Rule?
}
