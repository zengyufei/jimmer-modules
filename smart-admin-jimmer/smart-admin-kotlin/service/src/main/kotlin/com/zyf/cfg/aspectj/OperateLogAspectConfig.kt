package com.zyf.cfg.aspectj

import org.springframework.context.annotation.Configuration

/**
 * 操作日志切面 配置
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-05-30 21:22:12
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Configuration
class OperateLogAspectConfig : OperateLogAspect() {
    override val operateLogConfig: OperateLogConfig
        /**
         * 配置信息
         */
        get() {
            val config = OperateLogConfig()
            config.corePoolSize = 1
            config.queueCapacity = 10000
            return config
        }
}