package com.zyf.support.repeatsubmit.ticket

import com.zyf.support.repeatsubmit.annoation.RepeatSubmit
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit
import java.util.function.Function

/**
 * 凭证（redis实现）
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020-11-25 20:56:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class RepeatSubmitRedisTicket(
    private val redisValueOperations: ValueOperations<String?, String>,
    ticketFunction: Function<String, String>
) : AbstractRepeatSubmitTicket(ticketFunction) {
    override fun getTicketTimestamp(ticket: String?): Long? {
        var timeStamp = System.currentTimeMillis()
        val setFlag = redisValueOperations.setIfAbsent(ticket!!, timeStamp.toString(), RepeatSubmit.MAX_INTERVAL.toLong(), TimeUnit.MILLISECONDS)!!
        if (!setFlag) {
            timeStamp = redisValueOperations[ticket]!!.toLong()
        }
        return timeStamp
    }

    override fun putTicket(ticket: String?) {
        redisValueOperations.operations.delete(ticket!!)
        this.getTicketTimestamp(ticket)
    }

    override fun removeTicket(ticket: String?) {
        redisValueOperations.operations.delete(ticket!!)
    }
}
