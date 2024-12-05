package com.zyf.cfg.listener

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.utils.SmartIpUtil.init
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.boot.context.logging.LoggingApplicationListener
import org.springframework.context.ApplicationListener
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.IOException

/**
 * 初初始化ip工具类
 *
 * @Author 1024创新实验室: zhuoda
 * @Date 2023-09-03 23:45:26
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Order(value = LoggingApplicationListener.DEFAULT_ORDER)
@Slf4j
class Ip2RegionListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    override fun onApplicationEvent(applicationEvent: ApplicationEnvironmentPreparedEvent) {
        val environment = applicationEvent.environment
        val logDirectoryPath = environment.getProperty(LOG_DIRECTORY)
            ?: throw ExceptionInInitializerError("环境变量为空：" + LOG_DIRECTORY)
        System.setProperty(LOG_DIRECTORY, logDirectoryPath)

        // 1、从jar中的ip2region.xdb文件复制到服务器目录中
        val logDirectoryFile = File(logDirectoryPath)
        if (!logDirectoryFile.exists()) {
            logDirectoryFile.mkdirs()
        }

        val tempFilePath: String = if (logDirectoryPath.endsWith("/")) {
            logDirectoryPath + IP_FILE_NAME
        } else {
            logDirectoryPath + "/" + IP_FILE_NAME
        }

        val tempFile = File(tempFilePath)
        try {

            IoUtil.copy(ClassPathResource(IP_FILE_NAME).inputStream, FileUtil.getOutputStream(tempFile))

            // 2、初始化
            init(tempFilePath)
        } catch (e: IOException) {
            log.error("无法复制ip数据文件 ip2region.xdb", e)
            throw ExceptionInInitializerError("无法复制ip数据文件")
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }


    companion object {
        private const val IP_FILE_NAME = "ip2region.xdb"

        private const val LOG_DIRECTORY = "project.log-directory"
    }
}