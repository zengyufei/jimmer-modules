package com.zyf.support.job.api.domain

import com.zyf.common.base.BaseEnum

/**
 * 定时任务 发布/订阅消息对象
 *
 * @author huke
 * @date 2024/6/20 21:10
 */
class SmartJobMsg {
    /**
     * 消息id 无需设置
     */
    var msgId: String? = null

    /**
     * 任务id
     */
    var jobId: String? = null

    /**
     * 任务参数
     */
    var param: String? = null

    /**
     * 消息类型
     */
    var msgType: MsgTypeEnum? = null

    /**
     * 更新人
     */
    var updateName: String? = null

    enum class MsgTypeEnum(override val value: Int, override val desc: String) : BaseEnum {
        /**
         * 1 更新任务
         */
        UPDATE_JOB(1, "更新任务"),

        EXECUTE_JOB(2, "执行任务"),
        ;

    }
}
