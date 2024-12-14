package com.zyf.support.job.config

import com.zyf.support.job.config.SmartJobConfig
import org.springframework.boot.context.properties.ConfigurationProperties


/**
 * smart job 配置
 * 与配置文件参数对应
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
@ConfigurationProperties(prefix = SmartJobConfig.CONFIG_PREFIX)
class SmartJobConfig {
    /**
     * 任务执行核心线程数 偶数 默认2
     */
    var corePoolSize: Int = 2

    /**
     * 任务延迟初始化 默认30秒
     */
    var initDelay: Int = 30

    /**
     * 数据库配置检测-开关 默认开启
     */
    var dbRefreshEnabled: Boolean = true

    /**
     * 数据库配置检测-执行间隔 默认120秒
     */
    var dbRefreshInterval: Int = 120

    companion object {
        const val CONFIG_PREFIX: String = "smart.job"
    }
}
