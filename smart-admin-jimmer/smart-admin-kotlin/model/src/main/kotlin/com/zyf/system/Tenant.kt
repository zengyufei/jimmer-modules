package com.zyf.system

import com.fasterxml.jackson.annotation.JsonFormat
import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.Table
import java.time.LocalDateTime

@Entity
@Table(name = "t_tenant")
interface Tenant  : BaseEntity {

    /** 租户ID */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name="id")
    val tenantId: String

    /** 租户名称 */
    @Column(name="name")
    val tenantName: String

    /** 租户编码 */
    @Column(name="code")
    val tenantCode: String

    /** 租户状态，0正常，1停用 */
    @Column(name="status")
    val status: String?

    /** 租户开始时间 */
    @Column(name="start_time")
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val startTime: LocalDateTime?

    /** 租户结束时间 */
    @Column(name="end_time")
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val endTime: LocalDateTime?

}