package com.zyf.system

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_role")
interface Role : TenantAware, BaseEntity {

    /** 角色id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "role_id")
    val roleId: String

    /** 角色名称 */
    @Key
    @Column(name = "role_name")
    val roleName: String

    /** 角色编码 */
    @Key
    @Column(name = "role_code")
    val roleCode: String?

    /** 角色描述 */
    @Column(name = "remark")
    val remark: String?

    @IdView("menus")
    val menuIds: List<String>

    @ManyToMany(mappedBy = "roles")
    val employees: List<Employee>

    @ManyToMany
    @JoinTable(
        name = "t_role_menu",
        joinColumnName = "role_id",
        inverseJoinColumnName = "menu_id"
    )
    val menus: List<Menu>

    @OneToMany(mappedBy = "role")
    val roleDataScopes: List<RoleDataScope>

}