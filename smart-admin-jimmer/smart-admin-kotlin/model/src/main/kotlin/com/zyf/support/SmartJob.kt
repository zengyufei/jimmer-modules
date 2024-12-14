package com.zyf.support;

import java.time.LocalDateTime;

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * 定时任务配置 @listen(SmartJob)实体类
 *
 * @author makejava
 * @since 2024-12-12 21:34:58
 */
@Entity
@Table(name = "t_smart_job")
interface SmartJob : TenantAware, BaseEntity {

    /**
     * 任务id
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "job_id")
    val jobId: String

    /**
     * 任务名称
     */
    @Column(name = "job_name")
    val jobName: String

    /**
     * 任务执行类
     */
    @Column(name = "job_class")
    val jobClass: String

    /**
     * 触发类型
     */
    @Column(name = "trigger_type")
    val triggerType: String

    /**
     * 触发配置
     */
    @Column(name = "trigger_value")
    val triggerValue: String

    /**
     * 是否开启
     */
    @Column(name = "enabled_flag")
    val enabledFlag: Boolean

    /**
     * 参数
     */
    @Column(name = "param")
    val param: String?

    /**
     * 最后一次执行时间
     */
    @Column(name = "last_execute_time")
    val lastExecuteTime: LocalDateTime?

    /**
     * 最后一次执行记录id
     */
    @Column(name = "last_execute_log_id")
    val lastExecuteLogId: String?

    /**
     * 排序
     */
    @Column(name = "sort")
    val sort: Int

    /**
     * remark
     */
    @Column(name = "remark")
    val remark: String?

    /**
     * 更新人
     */
    @Column(name = "update_name")
    val updateName: String


}

