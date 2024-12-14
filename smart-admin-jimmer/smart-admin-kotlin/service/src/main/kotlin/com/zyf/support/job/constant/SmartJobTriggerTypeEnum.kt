package com.zyf.support.job.constant

import com.zyf.common.base.BaseEnum


/**
 * job 任务触发类型 枚举类
 *
 * @author huke
 * @date 2024年6月29日
 */
enum class SmartJobTriggerTypeEnum(
    override val value: String,
    override val desc: String,
) : BaseEnum {
    /**
     * 1 cron表达式
     */
    CRON("cron", "cron表达式"),

    FIXED_DELAY("fixed_delay", "固定间隔"),
    ;

}

