package com.zyf.support.job.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.support.Config
import com.zyf.support.job.core.ISmartJob
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 定时任务 示例2
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
@Slf4j
@Service
class SmartJobSample2(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) : ISmartJob {

    /**
     * 定时任务示例
     * 需要事务时 添加 @Transactional 注解
     *
     * @param param 可选参数 任务不需要时不用管
     * @return
     */
    @Transactional(rollbackFor = [Throwable::class])
    override fun run(param: String?): String? {
        // 随便更新点什么东西

        val configEntity1 = Config {
            configId = "1"
            param?.let { remark = it }
        }
        sql.update(configEntity1)

        val configEntity2 = Config {
            configId = "2"
            remark = "SmartJob Sample2 update"
        }
        sql.update(configEntity2)

        return "执行成功,本次处理数据1条"
    }
}
