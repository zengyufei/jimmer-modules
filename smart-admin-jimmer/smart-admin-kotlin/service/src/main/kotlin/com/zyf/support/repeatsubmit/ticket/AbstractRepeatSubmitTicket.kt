package com.zyf.support.repeatsubmit.ticket

import java.util.function.Function

/**
 * 凭证（用于校验重复提交的东西）
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020-11-25 20:56:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
abstract class AbstractRepeatSubmitTicket(private val ticketFunction: Function<String, String>) {
    /**
     * 获取凭证
     *
     * @param ticketToken
     * @return
     */
    fun getTicket(ticketToken: String): String {
        return ticketFunction.apply(ticketToken)
    }

    /**
     * 获取凭证 时间戳
     *
     * @param ticket
     * @return
     */
    abstract fun getTicketTimestamp(ticket: String?): Long?


    /**
     * 设置本次请求时间
     *
     * @param ticket
     */
    abstract fun putTicket(ticket: String?)

    /**
     * 移除凭证
     *
     * @param ticket
     */
    abstract fun removeTicket(ticket: String?)
}
