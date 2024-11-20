package runner.persistance.repository

import org.springframework.data.jpa.repository.JpaRepository
import runner.model.enums.RuleTypeEnum
import runner.persistance.entity.Rule

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