package runner.persistance.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import runner.model.enums.RuleTypeEnum
import java.util.*

@Entity
@Table(name = "rules")
data class Rule(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = UUID.randomUUID().toString(),
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "isActive", nullable = false)
    var isActive: Boolean,
    @Column(name = "value", nullable = false)
    var value: String? = null, // Optional
    @Enumerated(EnumType.STRING)
    val type: RuleTypeEnum,
)
