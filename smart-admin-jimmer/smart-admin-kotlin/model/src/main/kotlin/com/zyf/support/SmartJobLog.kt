package com.zyf.support;

import java.time.LocalDateTime;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * 定时任务-执行记录 @listen(SmartJobLog)实体类
 *
 * @author makejava
 * @since 2024-12-12 21:34:58
 */
@Entity
@Table(name = "t_smart_job_log")
interface SmartJobLog : TenantAware, BaseEntity {

    /**
     * logId
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "log_id")
    val logId: String

    /**
     * 任务id
     */
    @Column(name = "job_id")
    val jobId: String

    /**
     * 任务名称
     */
    @Column(name = "job_name")
    val jobName: String

    /**
     * 执行参数
     */
    @Column(name = "param")
    val param: String?

    /**
     * 是否成功
     */
    @Column(name = "success_flag")
    val successFlag: Boolean

    /**
     * 执行开始时间
     */
    @Column(name = "execute_start_time")
    val executeStartTime: LocalDateTime

    /**
     * 执行时长
     */
    @Column(name = "execute_time_millis")
    val executeTimeMillis: Long?

    /**
     * 执行结束时间
     */
    @Column(name = "execute_end_time")
    val executeEndTime: LocalDateTime?

    /**
     * executeResult
     */
    @Column(name = "execute_result")
    val executeResult: String?

    /**
     * ip
     */
    @Column(name = "ip")
    val ip: String

    /**
     * 进程id
     */
    @Column(name = "process_id")
    val processId: String

    /**
     * 程序目录
     */
    @Column(name = "program_path")
    val programPath: String

    /**
     * 创建人
     */
    @Column(name = "create_name")
    val createName: String


}

