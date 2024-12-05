package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.common.enums.ChangeLogTypeEnum
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*
import java.time.LocalDate

/** 意见反馈 */
@Entity
@Table(name = "t_feedback")
interface Feedback : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "feedback_id")
    val feedbackId: String

    /** 反馈内容 */
    @Column(name = "feedback_content")
    val feedbackContent: String?

    /** 反馈图片 */
    @Column(name = "feedback_attachment")
    val feedbackAttachment: String?

    /** 创建人用户类型 */
    @Column(name = "user_type")
    val userType: Int

    /** 创建人姓名 */
    @Column(name = "create_name")
    val userName: String
}