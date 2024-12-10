package com.zyf.cfg

import com.zyf.support.heartbeat.core.HeartBeatManager
import com.zyf.support.heartbeat.core.IHeartBeatRecordHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 心跳配置
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2018/10/9 18:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Configuration
class HeartBeatConfig(
    /**
     * 间隔时间
     */
    @Value("\${heart-beat.interval-seconds}")
    val intervalSeconds: Long,
    val heartBeatRecordHandler: IHeartBeatRecordHandler,
) {

    @Bean
    fun heartBeatManager(): HeartBeatManager {
        return HeartBeatManager(intervalSeconds * 1000L, heartBeatRecordHandler)
    }
}
