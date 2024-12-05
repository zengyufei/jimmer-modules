package com.zyf.employee

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_position")
interface Position : TenantAware, BaseEntity {

    /** 职务ID */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "position_id")
    val positionId: String

    /** 职务名称 */
    @Column(name = "position_name")
    val positionName: String

    /** 职级 */
    @Column(name = "level")
    val level: String?

    /** 排序 */
    @Column(name = "sort")
    val sort: Int?

    /** 备注 */
    @Column(name = "remark")
    val remark: String?
}