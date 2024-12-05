package com.zyf.oa

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * OA办公-OA银行信息
 */
@Entity
@Table(name = "t_oa_bank")
interface Bank : TenantAware, BaseEntity {

    /** 银行信息ID */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "bank_id")
    val bankId: String

    /** 开户银行 */
    @Column(name = "bank_name")
    val bankName: String

    /** 账户名称 */
    @Column(name = "account_name")
    val accountName: String

    /** 账号 */
    @Column(name = "account_number")
    val accountNumber: String

    /** 备注 */
    @Column(name = "remark")
    val remark: String?

    /** 是否对公 */
    @Column(name = "business_flag")
    val businessFlag: Boolean

    /** 企业ID */
    @IdView("enterprise")
    val enterpriseId: String

    /** 禁用状态 */
    @Column(name = "disabled_flag")
    val disabledFlag: Boolean

    /** 与企业的关联关系 */
    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    val enterprise: Enterprise
}