package com.zyf.support;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * reload结果(ReloadResult)实体类
 *
 * @author makejava
 * @since 2024-12-10 11:38:18
 */
@Entity
@Table(name = "t_reload_result")
interface ReloadResult : TenantAware, BaseEntity {

    /**
     * reloadResultId
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "reload_result_id")
    val reloadResultId: String

    /**
     * 项名称
     */
    @Column(name = "tag")
    val tag: String

    /**
     * 运行标识
     */
    @Column(name = "identification")
    val identification: String

    /**
     * args
     */
    @Column(name = "args")
    val args: String?

    /**
     * 是否成功 
     */
    @Column(name = "result")
    val result: Boolean

    /**
     * exception
     */
    @Column(name = "exception")
    val exception: String?

    /**
     * reloadItemId
     */
    @IdView("reloadItem")
    val reloadItemId: String?

    /**
     * reloadItemId
     */
    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "reload_item_id")
    val reloadItem: ReloadItem?


}

