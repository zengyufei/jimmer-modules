package com.zyf.employee

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.oa.Notice
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_department")
interface Department  : TenantAware, BaseEntity {

    /** 部门主键id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name="department_id")
    val departmentId: String

    /** 部门名称 */
    @Column(name="name")
    val departmentName: String

    /** 部门负责人id */
    @IdView("manager")
    val managerId: String?

    /** 部门的父级id */
    @IdView("parent")
    val parentId: String?

    /** 部门排序 */
    @Column(name = "sort")
    val sort: Int

    @ManyToOne
    @JoinColumn(name = "manager_id")
    val manager: Employee?

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: Department?

    @OneToMany(mappedBy = "parent")
    val children: List<Department>

    @OneToMany(mappedBy = "department")
    val employees: List<Employee>

    @ManyToMany(mappedBy = "departmentRanges")
    val notices: List<Notice>
}