package com.zyf.support;

import java.time.LocalDateTime;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import org.babyfish.jimmer.sql.*

/**
 * 公用服务 - 服务心跳(HeartBeatRecord)实体类
 *
 * @author makejava
 * @since 2024-12-09 17:59:38
 */
@Entity
@Table(name = "t_heart_beat_record")
interface HeartBeatRecord :  BaseEntity {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "heart_beat_record_id")
    val heartBeatRecordId: String

    /**
     * 项目名称
     */
    @Column(name = "project_path")
    val projectPath: String

    /**
     * 服务器ip
     */
    @Column(name = "server_ip")
    val serverIp: String

    /**
     * 进程号
     */
    @Column(name = "process_no")
    val processNo: String

    /**
     * 进程开启时间
     */
    @Column(name = "process_start_time")
    val processStartTime: LocalDateTime

    /**
     * 心跳时间
     */
    @Column(name = "heart_beat_time")
    val heartBeatTime: LocalDateTime


}

