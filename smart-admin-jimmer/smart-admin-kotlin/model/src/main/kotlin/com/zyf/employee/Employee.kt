package com.zyf.employee

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.common.enums.GenderEnum
import com.zyf.loginLog.LoginFail
import com.zyf.loginLog.LoginLog
import com.zyf.loginLog.OperateLog
import com.zyf.loginLog.PasswordLog
import com.zyf.oa.*
import com.zyf.support.FileInfo
import com.zyf.system.Role
import org.babyfish.jimmer.Formula
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_employee")
interface Employee : TenantAware, BaseEntity {

    /** 员工id/用户id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name="employee_id")
    val employeeId: String

    /** 登录用户名 */
    @Key
    @Column(name="login_name")
    val loginName: String

    /** 登录密码 */
    @Column(name="login_pwd")
    val loginPwd: String

    /** 员工名称 */
    @Column(name="actual_name")
    val actualName: String

    /** 头像 */
    @Column(name="avatar")
    val avatar: String?

    /** 员工名称, 0:女 1:男 2:未知 */
    @Column(name="gender")
    val gender: GenderEnum

    /** 手机号码 */
    @Column(name="phone")
    val phone: String?

    /** 职务ID */
    @IdView("position")
    val positionId: String?

    /**  部门ID */
    @IdView("department")
    val departmentId: String?

    /**  企业ID */
    @IdView("enterprise")
    val enterpriseId: String?

    /**  角色IDs */
    @IdView("roles")
    val roleIds: List<String>

    /** 邮箱 */
    @Column(name="email")
    val email: String?
    /** 是否被禁用 0否1是 */
    @Column(name="disabled_flag")
    val disabledFlag: Boolean
    /** 是否为超级管理员: 0 不是，1是 */
    @Column(name="administrator_flag")
    val administratorFlag: Boolean
    /** 备注 */
    @Column(name="remark")
    val remark: String?

    /** 职务 */
    @OneToOne
    @JoinColumn(name = "position_id")
    val position: Position?

    /** 部门 */
    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "department_id")
    val department: Department?

    @ManyToMany
    @JoinTable(
        name = "t_role_employee",
        joinColumnName = "employee_id",
        inverseJoinColumnName = "role_id"
    )
    val roles: List<Role>

    @OneToMany(mappedBy = "employee")
    val loginLogs: List<LoginLog>

    @OneToMany(mappedBy = "employee")
    val loginFails: List<LoginFail>

    @OneToMany(mappedBy = "employee")
    val operateLogs: List<OperateLog>

    @OneToMany(mappedBy = "creator")
    val fileInfos: List<FileInfo>

    @OneToMany(mappedBy = "employee")
    val noticeViewRecords: List<NoticeViewRecord>

    @OneToMany(mappedBy = "employee")
    val passwordLogs: List<PasswordLog>

    /** 员工所在的企业 */
    @ManyToOne(inputNotNull = true)
    @JoinTable(
        name = "t_oa_enterprise_employee",
        joinColumnName = "employee_id",
        inverseJoinColumnName = "enterprise_id"
    )
    val enterprise: Enterprise?

    @Formula(dependencies = ["roles.roleName"])
    val roleNameList: List<String>
        get() = roles.map { it.roleName }


    @ManyToMany(mappedBy = "employeeRanges")
    val notices: List<Notice>
}