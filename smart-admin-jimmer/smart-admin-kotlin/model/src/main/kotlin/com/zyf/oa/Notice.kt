package com.zyf.oa

import com.fasterxml.jackson.annotation.JsonFormat
import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Department
import com.zyf.employee.Employee
import org.babyfish.jimmer.Formula
import org.babyfish.jimmer.sql.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Entity
@Table(name = "t_notice")
interface Notice : TenantAware, BaseEntity {

    /** 通知id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "notice_id")
    val noticeId: String

    /** 类型1公告 2动态 */
    @IdView("noticeType")
    val noticeTypeId: String?

    /** 标题 */
    @Column(name = "title")
    val title: String

    /** 是否全部可见 */
    @Column(name = "all_visible_flag")
    val allVisibleFlag: Boolean

    /** 是否定时发布 */
    @Column(name = "scheduled_publish_flag")
    val scheduledPublishFlag: Boolean

    /** 发布时间 */
    @Column(name = "publish_time")
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @get:DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val publishTime: LocalDateTime

    /** 文本内容 */
    @Column(name = "content_text")
    val contentText: String

    /** html内容 */
    @Column(name = "content_html")
    val contentHtml: String

    /** 附件 */
    @Column(name = "attachment")
    val attachment: String?

    /** 页面浏览量，传说中的pv */
    @Default("0")
    @Column(name = "page_view_count")
    val pageViewCount: Int

    /** 用户浏览量，传说中的uv */
    @Default("0")
    @Column(name = "user_view_count")
    val userViewCount: Int

    /** 来源 */
    @Column(name = "source")
    val source: String?

    /** 作者 */
    @Column(name = "author")
    val author: String?

    /** 文号，如：1024创新实验室发〔2022〕字第36号 */
    @Column(name = "document_number")
    val documentNumber: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "notice_type_id")
    val noticeType: NoticeType?

    @OneToMany(mappedBy = "notice")
    val noticeViewRecords: List<NoticeViewRecord>

    @Formula(dependencies = ["publishTime"])
    val publishFlag: Boolean
        get() = publishTime.isBefore(LocalDateTime.now())

    @ManyToMany
    @JoinTable(
        name = "t_notice_employee_range",
        joinColumnName = "notice_id",
        inverseJoinColumnName = "employee_id"
    )
    val employeeRanges: List<Employee>

    @ManyToMany
    @JoinTable(
        name = "t_notice_department_range",
        joinColumnName = "notice_id",
        inverseJoinColumnName = "department_id"
    )
    val departmentRanges: List<Department>
}