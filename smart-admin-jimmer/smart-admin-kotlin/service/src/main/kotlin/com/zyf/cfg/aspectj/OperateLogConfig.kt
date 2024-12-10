package com.zyf.cfg.aspectj

import com.zyf.loginLog.OperateLog
import java.util.function.Function

class OperateLogConfig {
    /**
     * 操作日志存储方法
     */
    var saveFunction: Function<OperateLog, Boolean>? = null

    /**
     * 核心线程数
     */
    var corePoolSize: Int? = null

    /**
     * 队列大小
     */
    var queueCapacity: Int? = null
}