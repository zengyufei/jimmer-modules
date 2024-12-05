package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.common.enums.ChangeLogTypeEnum
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*
import java.time.LocalDate
import java.time.LocalDateTime

/** 通知消息 */
@Entity
@Table(name = "t_message")
interface Message : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "message_id")
    val messageId: String

    /** 消息类型 */
    @Column(name = "message_type")
    val messageType: Int

    /** 接收者用户类型 */
    @Column(name = "receiver_user_type")
    val receiverUserType: Int

    /** 接收者用户id */
    @Column(name = "receiver_user_id")
    val receiverUserId: String

    /** 相关数据id */
    @Column(name = "data_id")
    val dataId: String?

    /** 标题 */
    @Column(name = "title")
    val title: String

    /** 内容 */
    @Column(name = "content")
    val content: String

    /** 是否已读 */
    @Column(name = "read_flag")
    val readFlag: Boolean

    /** 已读时间 */
    @Column(name = "read_time")
    val readTime: LocalDateTime?
}