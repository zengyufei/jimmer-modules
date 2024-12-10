package com.zyf.oa

import com.zyf.common.annotations.DataTracerFieldEnum
import com.zyf.common.annotations.DataTracerFieldLabel
import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.common.enums.EnterpriseTypeEnum
import com.zyf.employee.Employee
import com.zyf.support.FileInfo
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_oa_enterprise")
interface



Enterprise : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "enterprise_id")
    val enterpriseId: String

    /** 企业名称 */
    @Column(name = "enterprise_name")
    @DataTracerFieldLabel("企业名称")
    val enterpriseName: String

    /** 企业logo */
    @Column(name = "enterprise_logo")
    @DataTracerFieldLabel("企业logo")
    val enterpriseLogo: String?

    /** 类型（1:有限公司;2:合伙公司） */
    @Column(name = "type")
    @DataTracerFieldLabel("类型")
    @DataTracerFieldEnum(enumClass = EnterpriseTypeEnum::class)
    val type: EnterpriseTypeEnum

    /** 统一社会信用代码 */
    @Column(name = "unified_social_credit_code")
    @DataTracerFieldLabel("统一社会信用代码")
    val unifiedSocialCreditCode: String

    /** 联系人 */
    @Column(name = "contact")
    @DataTracerFieldLabel("联系人")
    val contact: String

    /** 联系人电话 */
    @Column(name = "contact_phone")
    @DataTracerFieldLabel("联系人电话")
    val contactPhone: String

    /** 邮箱 */
    @Column(name = "email")
    @DataTracerFieldLabel("邮箱")
    val email: String?

    /** 省份 */
    @Column(name = "province")
    val province: String?

    /** 省份名称 */
    @Column(name = "province_name")
    @DataTracerFieldLabel("省份名称")
    val provinceName: String?

    /** 市 */
    @Column(name = "city")
    val city: String?

    /** 城市名称 */
    @Column(name = "city_name")
    @DataTracerFieldLabel("城市名称")
    val cityName: String?

    /** 区县 */
    @Column(name = "district")
    val district: String?

    /** 区县名称 */
    @Column(name = "district_name")
    @DataTracerFieldLabel("区县名称")
    val districtName: String?

    /** 详细地址 */
    @Column(name = "address")
    @DataTracerFieldLabel("详细地址")
    val address: String?

    /** 营业执照 */
    @Column(name = "business_license")
    @DataTracerFieldLabel("营业执照")
    val businessLicense: String?

    /** 禁用状态 */
    @Column(name = "disabled_flag")
    @DataTracerFieldLabel("禁用状态")
    val disabledFlag: Boolean

    @OneToMany(mappedBy = "enterprise")
    val employees: List<Employee>

    @OneToMany(mappedBy = "enterprise")
    val banks: List<Bank>

}