package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.common.enums.ChangeLogTypeEnum
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*
import java.time.LocalDate

/** 系统配置 */
@Entity
@Table(name = "t_change_log")
interface ChangeLog : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "change_log_id")
    val changeLogId: String

    /** 版本 */
    @Key
    @Column(name = "version")
    val version: String

    /** 更新类型:[1:特大版本功能更新;2:功能更新;3:bug修复] */
    @Column(name = "type")
    val type: Int

    /** 发布人 */
    @IdView("publishAuthor")
    val publishAuthorId: String?

    /** 发布日期 */
    @Column(name = "public_date")
    val publicDate: LocalDate

    /** 更新内容 */
    @Column(name = "content")
    val content: String

    /** 跳转链接 */
    @Column(name = "link")
    val link: String?

    /** 发布人 */
    @OneToOne(inputNotNull = true)
    @JoinColumn(name="publish_author", foreignKeyType = ForeignKeyType.FAKE)
    val publishAuthor: Employee?

}