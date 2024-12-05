package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/** 系统配置 */
@Entity
@Table(name = "t_config")
interface Config : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "config_id")
    val configId: String

    /** 参数名字 */
    @Column(name = "config_name")
    val configName: String

    /** 参数key */
    @Key
    @Column(name = "config_key")
    val configKey: String

    /** 参数value */
    @Column(name = "config_value")
    val configValue: String

    /** 备注 */
    @Column(name = "remark")
    val remark: String?

}