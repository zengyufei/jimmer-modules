package com.zyf.system

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_role_data_scope")
interface RoleDataScope : TenantAware, BaseEntity {

    /** 数据范围id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "id")
    val roleDataScopeId: String

    /** 数据范围类型 */
    @Column(name = "data_scope_type")
    val dataScopeType: Int

    /** 显示类型 */
    @Column(name = "view_type")
    val viewType: Int

    /** 角色id */
    @IdView("role")
    val roleId: String

    @Key
    @ManyToOne
    @JoinColumn(name = "role_id")
    val role: Role
}