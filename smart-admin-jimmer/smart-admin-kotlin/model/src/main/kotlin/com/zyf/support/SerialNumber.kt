package com.zyf.support;

import java.time.LocalDateTime;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * 单号生成器定义表(SerialNumber)实体类
 *
 * @author makejava
 * @since 2024-12-09 20:36:31
 */
@Entity
@Table(name = "t_serial_number")
interface SerialNumber : TenantAware, BaseEntity {

    /**
     * serialNumberId
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "serial_number_id")
    val serialNumberId: String

    /**
     * 业务名称
     */
    @Column(name = "business_name")
    val businessName: String

    /**
     * 格式[yyyy]表示年,[mm]标识月,[dd]表示日,[nnn]表示三位数字
     */
    @Column(name = "format")
    val format: String?

    /**
     * 规则格式。none没有周期, year 年周期, month月周期, day日周期
     */
    @Column(name = "rule_type")
    val ruleType: String

    /**
     * 初始值
     */
    @Column(name = "init_number")
    val initNumber: Long

    /**
     * 步长随机数
     */
    @Column(name = "step_random_range")
    val stepRandomRange: Int

    /**
     * 备注
     */
    @Column(name = "remark")
    val remark: String?

    /**
     * 上次产生的单号, 默认为空
     */
    @Column(name = "last_number")
    val lastNumber: Long?

    /**
     * 上次产生的单号时间
     */
    @Column(name = "last_time")
    val lastTime: LocalDateTime?


}

