package com.zyf.helpDoc;

import java.time.LocalDateTime;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * 帮助文档-查看记录(HelpDocViewRecord)实体类
 *
 * @author makejava
 * @since 2024-12-14 16:24:54
 */
@Entity
@Table(name = "t_help_doc_view_record")
interface HelpDocViewRecord : TenantAware, BaseEntity {

    /**
     * 通知公告id
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "help_doc_id")
    val helpDocId: String

    /**
     * 用户id
     */
    @Column(name = "user_id")
    val userId: String

    /**
     * 用户名称
     */
    @Column(name = "user_name")
    val userName: String?

    /**
     * 查看次数
     */
    @Column(name = "page_view_count")
    val pageViewCount: Int?

    /**
     * 首次ip
     */
    @Column(name = "first_ip")
    val firstIp: String?

    /**
     * 首次用户设备等标识
     */
    @Column(name = "first_user_agent")
    val firstUserAgent: String?

    /**
     * 最后一次ip
     */
    @Column(name = "last_ip")
    val lastIp: String?

    /**
     * 最后一次用户设备等标识
     */
    @Column(name = "last_user_agent")
    val lastUserAgent: String?


}

