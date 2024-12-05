package com.zyf.loginLog

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*

/** 密码修改记录 */
@Entity
@Table(name = "t_password_log")
interface PasswordLog : TenantAware, BaseEntity {

    /** 主键 */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "id")
    val passwordLogId: String

    /** 用户id */
    @IdView("employee")
    val employeeId: String?

    /** 用户类型 */
    @Column(name = "user_type")
    val userType: Int

    /** 旧密码 */
    @Column(name = "old_password")
    val oldPassword: String

    /** 新密码 */
    @Column(name = "new_password")
    val newPassword: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "user_id")
    val employee: Employee?
}