package com.zyf.cfg.listener

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.boot.context.logging.LoggingApplicationListener
import org.springframework.context.ApplicationListener
import org.springframework.core.annotation.Order

/**
 * 将application.yam l中的日志路径变量:project.log-path注入到 log4j2.xml
 *
 * @Author 1024创新实验室: zhuoda
 * @Date 2023-09-03 23:45:26
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Order(value = LoggingApplicationListener.DEFAULT_ORDER - 1)
class LogVariableListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    override fun onApplicationEvent(applicationEvent: ApplicationEnvironmentPreparedEvent) {
        val environment = applicationEvent.environment
        val filePath = environment.getProperty(LOG_DIRECTORY)
        if (filePath != null) {
            System.setProperty(LOG_DIRECTORY, filePath)
        }
    }

    companion object {
        private const val LOG_DIRECTORY = "project.log-directory"
    }
}