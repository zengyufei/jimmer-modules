package com.zyf.cfg.aspectj

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.StringConst
import com.zyf.common.utils.SmartIpUtil
import com.zyf.common.utils.SmartRequestUtil.requestUser
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.boot.context.properties.bind.BindResult
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.ThreadPoolExecutor

/**
 * 操作日志记录处理,对所有OperateLog注解的Controller进行操作日志监控
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021-12-08 20:48:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Aspect
abstract class OperateLogAspect {
    @Resource
    private val applicationContext: ApplicationContext? = null

    @Resource
    private val objectMapper: ObjectMapper? = null

    /**
     * 线程池
     */
    private var taskExecutor: ThreadPoolTaskExecutor? = null

    abstract val operateLogConfig: OperateLogConfig

    init {
        this.initThread()
    }

    @Pointcut(POINT_CUT)
    fun logPointCut() {
    }

    @AfterReturning(pointcut = "logPointCut()")
    fun doAfterReturning(joinPoint: JoinPoint) {
        handleLog(joinPoint, null)
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    fun doAfterThrowing(joinPoint: JoinPoint, e: Exception?) {
        handleLog(joinPoint, e)
    }

    /**
     * 初始化线程池
     */
    private fun initThread() {
        val config = operateLogConfig
        var corePoolSize = Runtime.getRuntime().availableProcessors()
        config.corePoolSize?.let {
            corePoolSize = it
        }
        taskExecutor = ThreadPoolTaskExecutor()
        // 线程初始化
        taskExecutor!!.initialize()
        // 设置核心线程数
        taskExecutor!!.corePoolSize = corePoolSize
        // 设置最大线程数
        taskExecutor!!.maxPoolSize = corePoolSize * 2
        // 设置队列容量
        taskExecutor!!.queueCapacity = 1000
        // 设置线程活跃时间（秒）
        taskExecutor!!.keepAliveSeconds = 60
        // 设置默认线程名称
        taskExecutor!!.setThreadNamePrefix("smart-operate-log")
        // 设置拒绝策略
        taskExecutor!!.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        // 等待所有任务结束后再关闭线程池
        taskExecutor!!.setWaitForTasksToCompleteOnShutdown(true)
    }

    protected fun handleLog(joinPoint: JoinPoint, e: Exception?) {
        try {
            val operateLog = this.getAnnotationLog(joinPoint) ?: return
            this.submitLog(joinPoint, e)
        } catch (exp: Exception) {
            log.error("保存操作日志异常:{}", exp.message)
            exp.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun getAnnotationLog(joinPoint: JoinPoint): OperateLog? {
        val signature = joinPoint.signature
        val methodSignature = signature as MethodSignature
        val method = methodSignature.method
        val classAnnotation = AnnotationUtils.findAnnotation(method.declaringClass, OperateLog::class.java)
        if (classAnnotation != null) {
            return classAnnotation
        }
        val methodAnnotation = AnnotationUtils.findAnnotation(method, OperateLog::class.java)
        return methodAnnotation
    }

    /**
     * swagger tag
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    private fun getApi(joinPoint: JoinPoint): Tag? {
        val signature = joinPoint.signature
        val methodSignature = signature as MethodSignature
        val method = methodSignature.method
        val classAnnotation = AnnotationUtils.findAnnotation(method!!.declaringClass, Tag::class.java)
        if (method != null) {
            return classAnnotation
        }
        return null
    }

    /**
     * swagger ApiOperation
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    private fun getApiOperation(joinPoint: JoinPoint): Operation? {
        val signature = joinPoint.signature
        val methodSignature = signature as MethodSignature
        val method = methodSignature.method

        if (method != null) {
            return method.getAnnotation(Operation::class.java)
        }
        return null
    }

    /**
     * 提交存储操作日志
     *
     * @param joinPoint
     * @param e
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun submitLog(joinPoint: JoinPoint, e: Throwable?) {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        // 设置用户信息
        val user = requestUser ?: return

        val args = joinPoint.args
        val params = buildParamString(args)
        val className = joinPoint.target.javaClass.name
        val methodName = joinPoint.signature.name
        val operateMethod = "$className.$methodName"
        var failReason: String? = null
        var successFlag = true
        if (e != null) {
            successFlag = false
            failReason = getExceptionString(e)
        }

        val operateLog: com.zyf.loginLog.OperateLog = com.zyf.loginLog.OperateLog {
            employeeId = user.userId
            operateUserType = user.userType!!
            operateUserName = user.userName!!
            url = request.requestURI
            method = operateMethod
            param = params
            ip = user.ip
            ipRegion = SmartIpUtil.getRegion(user.ip)
            userAgent = user.userAgent
            this.failReason = failReason
            this.successFlag = successFlag

            val apiOperation = getApiOperation(joinPoint)
            if (apiOperation != null) {
                this.content = apiOperation.summary
            }
            val api = getApi(joinPoint)
            if (api != null) {
                val name = api.name
                this.module = StrUtil.join(",", name)
            }
        }


        taskExecutor!!.execute {
            this.saveLog(operateLog)
        }
    }

    private fun buildParamString(args: Array<Any>?): String {
        if (args == null || args.size == 0) {
            return StringConst.EMPTY
        }

        val filterArgs: MutableList<Any> = ArrayList()
        for (arg in args) {
            if (arg == null) {
                continue
            }
            if (arg is HttpServletRequest
                || arg is HttpServletResponse
                || arg is ModelAndView
                || arg is MultipartFile
                || arg is BindResult<*>
            ) {
                continue
            }
            filterArgs.add(arg)
        }
        return objectMapper!!.writeValueAsString(filterArgs)
    }


    private fun getExceptionString(e: Throwable): String {
        val sw = StringWriter()
        PrintWriter(sw).use { pw ->
            e.printStackTrace(pw)
        }
        return sw.toString()
    }

    /**
     * 保存操作日志
     *
     * @param operateLog
     * @return
     */
    private fun saveLog(operateLog: com.zyf.loginLog.OperateLog): Boolean? {
        val operateLogConfig = operateLogConfig
        if (operateLogConfig.saveFunction == null) {
            val sql = applicationContext!!.getBean(KSqlClient::class.java)
            sql.insert(operateLog)
            return true
        }
        return operateLogConfig.saveFunction?.apply(operateLog)
    }

    companion object {
        private const val POINT_CUT = "@within(com.zyf.common.annotations.OperateLog) || @annotation(com.zyf.common.annotations.OperateLog)"
    }
}
