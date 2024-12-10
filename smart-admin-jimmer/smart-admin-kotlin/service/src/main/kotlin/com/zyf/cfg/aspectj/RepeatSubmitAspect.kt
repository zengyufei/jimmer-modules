package com.zyf.cfg.aspectj

import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.support.repeatsubmit.annoation.RepeatSubmit
import com.zyf.support.repeatsubmit.ticket.AbstractRepeatSubmitTicket
import org.apache.commons.lang3.StringUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import kotlin.math.min

/**
 * 重复提交 aop切口
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2020-11-25 20:56:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Aspect
@Slf4j
class RepeatSubmitAspect
/**
 * 获取凭证信息
 *
 * @param repeatSubmitTicket
 */(private val repeatSubmitTicket: AbstractRepeatSubmitTicket) {
    /**
     * 定义切入点
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.zyf.support.repeatsubmit.annoation.RepeatSubmit)")
    @Throws(Throwable::class)
    fun around(point: ProceedingJoinPoint): Any? {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        val ticketToken = attributes!!.request.servletPath
        val ticket = repeatSubmitTicket.getTicket(ticketToken)
        if (StringUtils.isEmpty(ticket)) {
            return point.proceed()
        }
        val lastRequestTime = repeatSubmitTicket.getTicketTimestamp(ticket)
        if (lastRequestTime != null) {
            val method = (point.signature as MethodSignature).method
            val annotation = method.getAnnotation(RepeatSubmit::class.java)
            val interval = min(annotation.value.toDouble(), RepeatSubmit.MAX_INTERVAL.toDouble()).toInt()
            if (System.currentTimeMillis() < lastRequestTime + interval) {
                // 提交频繁
                return ResponseDTO.error<String>(UserErrorCode.REPEAT_SUBMIT)
            }
        }
        var obj: Any? = null
        try {
            // 先给 ticket 设置在执行中
            repeatSubmitTicket.putTicket(ticket)
            obj = point.proceed()
        } catch (throwable: Throwable) {
            log.error("", throwable)
            throw throwable
        }
        return obj
    }
}
