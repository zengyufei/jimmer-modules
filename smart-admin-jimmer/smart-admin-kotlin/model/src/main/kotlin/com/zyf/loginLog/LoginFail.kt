package com.zyf.loginLog

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "t_login_fail")
interface LoginFail : TenantAware, BaseEntity {

    /** 主键 */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "login_fail_id")
    val loginFailId: String

    /** 用户id */
    @IdView("employee")
    val employeeId: String?

    /** 用户类型 */
    @Column(name = "user_type")
    val userType: Int

    /** 登录名 */
    @Column(name = "login_name")
    val loginName: String?

    /** 连续登录失败次数 */
    @Column(name = "login_fail_count")
    val loginFailCount: Int?

    /** 锁定状态:1锁定，0未锁定 */
    @Column(name = "lock_flag")
    val lockFlag: Boolean?

    /** 连续登录失败锁定开始时间 */
    @Column(name = "login_lock_begin_time")
    val loginLockBeginTime: LocalDateTime?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "user_id")
    val employee: Employee?
}