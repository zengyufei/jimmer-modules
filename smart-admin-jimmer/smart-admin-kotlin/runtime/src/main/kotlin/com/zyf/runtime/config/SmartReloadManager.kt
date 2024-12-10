package com.zyf.runtime.config

import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.annotations.SmartReload
import com.zyf.runtime.domain.SmartReloadObject
import com.zyf.runtime.utils.SmartReloadRunnable
import jakarta.annotation.PreDestroy
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * SmartReloadManager 管理器
 *
 *
 * 可以在此类中添加 检测任务 以及注册 处理程序
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Service
class SmartReloadManager(
    @Value("\${reload.interval-seconds}")
    val intervalSeconds: Int,
    val reloadCommand: AbstractSmartReloadCommand,
) : ApplicationListener<ContextRefreshedEvent> {

    private var threadPoolExecutor: ScheduledThreadPoolExecutor? = null

    private var applicationContext: ApplicationContext? = null

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        this.applicationContext = event.applicationContext

        // 初始化
        this.init()
    }

    fun init() {
        this.registerReloadBean()

        if (threadPoolExecutor != null) {
            return
        }

        this.threadPoolExecutor = ScheduledThreadPoolExecutor(THREAD_COUNT) { r: Runnable? ->
            val t = Thread(r, THREAD_NAME_PREFIX)
            if (!t.isDaemon) {
                t.isDaemon = true
            }
            t
        }
        threadPoolExecutor!!.scheduleWithFixedDelay(SmartReloadRunnable(this.reloadCommand!!), 10, intervalSeconds!!.toLong(), TimeUnit.SECONDS)
    }

    @PreDestroy
    fun shutdown() {
        if (this.threadPoolExecutor != null) {
            threadPoolExecutor!!.shutdownNow()
            this.threadPoolExecutor = null
        }
    }

    /**
     * 注册 reload bean
     */
    fun registerReloadBean() {
        // 遍历所有Bean
        val beanNames = applicationContext!!.beanDefinitionNames
        for (beanName in beanNames) {
            val bean = applicationContext!!.getBean(beanName!!)
            ReflectionUtils.doWithMethods(bean.javaClass) { method: Method ->
                val smartReload = method.getAnnotation<SmartReload>(SmartReload::class.java) ?: return@doWithMethods
                val paramCount = method.parameterCount
                if (paramCount > 1) {
                    log.error("<<SmartReloadManager>> register tag reload : " + smartReload.value + " , param count cannot greater than one !")
                    return@doWithMethods
                }
                val reloadTag = smartReload.value
                this.register(reloadTag, SmartReloadObject(bean, method))
            }
        }
    }

    /**
     * 注册reload
     *
     * @param tag
     * @param smartReloadObject
     */
    private fun register(tag: String, smartReloadObject: SmartReloadObject) {
        if (RELOAD_OBJECT_MAP.containsKey(tag)) {
            log.error("<<SmartReloadManager>> register duplicated tag reload : $tag , and it will be cover!")
        }
        RELOAD_OBJECT_MAP[tag] = smartReloadObject
    }

    companion object {
        private val RELOAD_OBJECT_MAP: MutableMap<String, SmartReloadObject> = ConcurrentHashMap()

        private const val THREAD_NAME_PREFIX = "smart-reload"

        private const val THREAD_COUNT = 1

        /**
         * 获取重载对象
         *
         * @return
         */
        @JvmStatic
        fun getReloadObject(tag: String): SmartReloadObject? {
            return RELOAD_OBJECT_MAP[tag]
        }
    }
}
