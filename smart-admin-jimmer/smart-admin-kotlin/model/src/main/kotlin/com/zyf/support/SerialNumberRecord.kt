package com.zyf.support;

import java.time.LocalDateTime;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*
import java.time.LocalDate

/**
 * serial_number记录表(SerialNumberRecord)实体类
 *
 * @author makejava
 * @since 2024-12-09 20:36:32
 */
@Entity
@Table(name = "t_serial_number_record")
interface SerialNumberRecord : TenantAware, BaseEntity {

    /**
     * serialNumberRecordId
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "serial_number_record_id")
    val serialNumberRecordId: String

    /**
     * 记录日期
     */
    @Column(name = "record_date")
    val recordDate: LocalDate

    /**
     * 最后更新值
     */
    @Column(name = "last_number")
    val lastNumber: Long

    /**
     * 最后更新时间
     */
    @Column(name = "last_time")
    val lastTime: LocalDateTime

    /**
     * 更新次数
     */
    @Column(name = "count")
    val updateCount: Long

    /**
     * serialNumberId
     */
    @Column(name = "serial_number_id")
    val serialNumberId: String


}

