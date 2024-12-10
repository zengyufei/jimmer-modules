package com.zyf.support;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * reload项目(ReloadItem)实体类
 *
 * @author makejava
 * @since 2024-12-10 11:38:18
 */
@Entity
@Table(name = "t_reload_item")
interface ReloadItem : TenantAware, BaseEntity {

    /**
     * id
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "reload_item_id")
    val reloadItemId: String

    /**
     * 项名称
     */
    @Column(name = "tag")
    val tag: String

    /**
     * 参数 可选
     */
    @Column(name = "args")
    val args: String?

    /**
     * 运行标识
     */
    @Column(name = "identification")
    val identification: String

    @OneToMany(mappedBy = "reloadItem")
    val reloadResult: List<ReloadResult>
}

