package com.zyf.oa

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_notice_type")
interface NoticeType : TenantAware, BaseEntity {

    /** 通知类型id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "notice_type_id")
    val noticeTypeId: String

    /** 类型名称 */
    @Column(name = "notice_type_name")
    val noticeTypeName: String

    @OneToMany(mappedBy = "noticeType")
    val notices : List<Notice>
}