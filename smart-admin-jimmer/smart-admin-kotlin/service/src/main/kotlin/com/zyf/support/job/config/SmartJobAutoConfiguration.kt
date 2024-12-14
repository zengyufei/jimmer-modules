package com.zyf.support.job.config

import com.zyf.repository.support.SmartJobLogRepository
import com.zyf.repository.support.SmartJobRepository
import com.zyf.support.job.core.ISmartJob
import com.zyf.support.job.core.SmartJobLauncher
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 定时任务 配置
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
@Configuration
@EnableConfigurationProperties(SmartJobConfig::class)
@ConditionalOnProperty(prefix = SmartJobConfig.CONFIG_PREFIX, name = ["enabled"], havingValue = "true")
class SmartJobAutoConfiguration(
    private val jobConfig: SmartJobConfig,
    private val jobRepository: SmartJobRepository,
    private val jobLogRepository: SmartJobLogRepository,
    private val jobInterfaceList: List<ISmartJob>
) {
    /**
     * 定时任务启动器
     *
     * @return
     */
    @Bean
    fun initJobLauncher(redissonClient: RedissonClient): SmartJobLauncher {
        return SmartJobLauncher(jobConfig, jobRepository, jobLogRepository, jobInterfaceList, redissonClient)
    }
}
