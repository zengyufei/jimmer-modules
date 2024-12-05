package com.zyf.loginLog

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_login_log")
interface LoginLog : TenantAware, BaseEntity {

    /** 主键 */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "login_log_id")
    val loginLogId: String

    /** 用户id */
    @IdView("employee")
    val userId: String?

    /** 用户类型 */
    @Column(name = "user_type")
    val userType: Int

    /** 用户名 */
    @Column(name = "user_name")
    val userName: String

    /** 用户ip */
    @Column(name = "login_ip")
    val loginIp: String?

    /** 用户ip地区 */
    @Column(name = "login_ip_region")
    val loginIpRegion: String?

    /** user-agent信息 */
    @Column(name = "user_agent")
    val userAgent: String?

    /** 登录结果：0成功 1失败 2 退出 */
    @Column(name = "login_result")
    val loginResult: Int

    /** 备注 */
    @Column(name = "remark")
    val remark: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "user_id")
    val employee: Employee?
}