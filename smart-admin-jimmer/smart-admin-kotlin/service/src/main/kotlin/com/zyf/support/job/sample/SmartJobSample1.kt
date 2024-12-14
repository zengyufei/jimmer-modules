package com.zyf.support.job.sample

import com.zyf.common.annotations.Slf4j
import com.zyf.support.job.core.ISmartJob
import org.springframework.stereotype.Service

/**
 * 定时任务 示例1
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
@Slf4j
@Service
class SmartJobSample1 : ISmartJob {
    /**
     * 定时任务示例
     *
     * @param param 可选参数 任务不需要时不用管
     * @return
     */
    override fun run(param: String?): String? {
        // 写点什么业务逻辑
        println("SmartJobSample1 执行了")
        return "执行完毕,随便说点什么吧"
    }
}
