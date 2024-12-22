package com.zyf.support;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * (MailTemplate)实体类
 *
 * @author makejava
 * @since 2024-12-21 17:18:46
 */
@Entity
@Table(name = "t_mail_template")
interface MailTemplate : TenantAware, BaseEntity {

    /**
     * templateCode
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "template_code")
    val templateCode: String

    /**
     * 模板名称
     */
    @Column(name = "template_subject")
    val templateSubject: String

    /**
     * 模板内容
     */
    @Column(name = "template_content")
    val templateContent: String

    /**
     * 解析类型 string，freemarker
     */
    @Column(name = "template_type")
    val templateType: String

    /**
     * 是否禁用
     */
    @Column(name = "disable_flag")
    val disableFlag: Boolean


}

