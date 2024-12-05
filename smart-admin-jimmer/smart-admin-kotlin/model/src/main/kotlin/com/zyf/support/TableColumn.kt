package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_table_column")
interface TableColumn : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "table_column_id")
    val tableColumnId: String

    /** 用户id */
    @Column(name = "user_id")
    val userId: String

    /** 用户类型 */
    @Column(name = "user_type")
    val userType: Int

    /** 前端表格id */
    @Column(name = "table_id")
    val tableId: String

    /** 具体的表格列，存入的json */
    @Column(name = "columns")
    val columns: String?

}