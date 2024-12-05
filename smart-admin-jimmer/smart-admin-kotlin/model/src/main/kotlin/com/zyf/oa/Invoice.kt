package com.zyf.oa

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * OA发票信息
 */
@Entity
@Table(name = "t_oa_invoice")
interface Invoice : TenantAware, BaseEntity {

    /** 发票信息ID */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "invoice_id")
    val invoiceId: String

    /** 开票抬头 */
    @Column(name = "invoice_heads")
    val invoiceHeads: String

    /** 纳税人识别号 */
    @Column(name = "taxpayer_identification_number")
    val taxpayerIdentificationNumber: String

    /** 银行账户 */
    @Column(name = "account_number")
    val accountNumber: String

    /** 开户行 */
    @Column(name = "bank_name")
    val bankName: String

    /** 备注 */
    @Column(name = "remark")
    val remark: String?

    /** 企业ID */
    @IdView("enterprise")
    val enterpriseId: String

    /** 企业 */
    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    val enterprise: Enterprise

    /** 禁用状态 */
    @Column(name = "disabled_flag")
    val disabledFlag: Boolean
}