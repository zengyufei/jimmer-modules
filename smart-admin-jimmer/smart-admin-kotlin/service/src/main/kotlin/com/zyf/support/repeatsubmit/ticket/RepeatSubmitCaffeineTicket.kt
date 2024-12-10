package com.zyf.support.repeatsubmit.ticket

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.zyf.support.repeatsubmit.annoation.RepeatSubmit
import java.util.concurrent.TimeUnit
import java.util.function.Function

/**
 * 凭证（内存实现）
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020-11-25 20:56:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class RepeatSubmitCaffeineTicket(ticketFunction: Function<String, String>) : AbstractRepeatSubmitTicket(ticketFunction) {
    override fun getTicketTimestamp(ticket: String?): Long? {
        return cache.getIfPresent(ticket)
    }


    override fun putTicket(ticket: String?) {
        cache.put(ticket, System.currentTimeMillis())
    }

    override fun removeTicket(ticket: String?) {
        cache.invalidate(ticket)
    }

    companion object {
        /**
         * 限制缓存最大数量 超过后先放入的会自动移除
         * 默认缓存时间
         * 初始大小为：100万
         */
        private val cache: Cache<String?, Long> = Caffeine.newBuilder()
            .maximumSize((100 * 10000).toLong())
            .expireAfterWrite(RepeatSubmit.MAX_INTERVAL.toLong(), TimeUnit.MILLISECONDS).build()
    }
}