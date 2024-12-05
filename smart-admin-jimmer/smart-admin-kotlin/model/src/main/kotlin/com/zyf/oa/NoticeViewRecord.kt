package com.zyf.oa

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_notice_view_record")
interface NoticeViewRecord : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "notice_view_record_id")
    val noticeViewRecordId: String

    /** 通知id */
    @IdView("notice")
    val noticeId: String?

    /** 员工id */
    @IdView("employee")
    val employeeId: String?

    /** 查看次数 */
    @Column(name = "page_view_count")
    val pageViewCount: Int?

    /** 首次ip */
    @Column(name = "first_ip")
    val firstIp: String?

    /** 首次用户设备等标识 */
    @Column(name = "first_user_agent")
    val firstUserAgent: String?

    /** 最后一次ip */
    @Column(name = "last_ip")
    val lastIp: String?

    /** 最后一次用户设备等标识 */
    @Column(name = "last_user_agent")
    val lastUserAgent: String?

    /** 通知 */
    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "notice_id")
    val notice: Notice?

    /** 员工 */
    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "employee_id")
    val employee: Employee?

}