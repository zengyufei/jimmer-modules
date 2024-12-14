package com.zyf.cfg

import cn.hutool.core.util.StrUtil
import org.redisson.config.Config
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.stereotype.Component

/**
 *
 * redission对于password 为空处理有问题，重新设置下
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2024/7/16 01:04:18
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net) ，Since 2012
 */
@Component
class RedissonPasswordConfigurationCustomizer : RedissonAutoConfigurationCustomizer {
    override fun customize(configuration: Config) {
        if (configuration.isSingleConfig && StrUtil.isEmpty(configuration.useSingleServer().password)) {
            configuration.useSingleServer().setPassword(null)
        }

        if (configuration.isClusterConfig && StrUtil.isEmpty(configuration.useClusterServers().password)) {
            configuration.useClusterServers().setPassword(null)
        }
        if (configuration.isSentinelConfig && StrUtil.isEmpty(configuration.useSentinelServers().password)) {
            configuration.useSentinelServers().setPassword(null)
        }
    }
}