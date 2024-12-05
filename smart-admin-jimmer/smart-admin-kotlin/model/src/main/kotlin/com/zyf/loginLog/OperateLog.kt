package com.zyf.loginLog

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_operate_log")
interface OperateLog : TenantAware, BaseEntity {

    /** 主键 */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "operate_log_id")
    val operateLogId: String

    /** 用户id */
    @IdView("employee")
    val employeeId: String?

    /** 用户类型 */
    @Column(name = "operate_user_type")
    val operateUserType: Int

    /** 用户名称 */
    @Column(name = "operate_user_name")
    val operateUserName: String

    /** 操作模块 */
    @Column(name = "module")
    val module: String?

    /** 操作内容 */
    @Column(name = "content")
    val content: String?

    /** 请求路径 */
    @Column(name = "url")
    val url: String?

    /** 请求方法 */
    @Column(name = "method")
    val method: String?

    /** 请求参数 */
    @Column(name = "param")
    val param: String?

    /** 请求ip */
    @Column(name = "ip")
    val ip: String?

    /** 请求ip地区 */
    @Column(name = "ip_region")
    val ipRegion: String?

    /** 请求user-agent */
    @Column(name = "user_agent")
    val userAgent: String?

    /** 请求结果 0失败 1成功 */
    @Column(name = "success_flag")
    val successFlag: Int?

    /** 失败原因 */
    @Column(name = "fail_reason")
    val failReason: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "operate_user_id")
    val employee: Employee?
}