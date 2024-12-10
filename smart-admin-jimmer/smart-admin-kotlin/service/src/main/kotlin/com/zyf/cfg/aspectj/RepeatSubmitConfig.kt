package com.zyf.cfg.aspectj

import com.zyf.common.constant.StringConst
import com.zyf.common.utils.SmartRequestUtil.requestUserId
import com.zyf.support.repeatsubmit.ticket.RepeatSubmitCaffeineTicket
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 重复提交配置
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021/10/9 18:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Configuration
class RepeatSubmitConfig {
    @Bean
    fun repeatSubmitAspect(): RepeatSubmitAspect {
        val caffeineTicket = RepeatSubmitCaffeineTicket { servletPath: String -> this.ticket(servletPath) }
        return RepeatSubmitAspect(caffeineTicket)
    }

    /**
     * 获取指明某个用户的凭证
     */
    private fun ticket(servletPath: String): String {
        val userId = requestUserId ?: return StringConst.EMPTY
        return servletPath + "_" + userId
    }
}
