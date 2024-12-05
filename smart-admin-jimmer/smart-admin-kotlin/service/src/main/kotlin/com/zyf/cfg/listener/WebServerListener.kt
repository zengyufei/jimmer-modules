package com.zyf.cfg.listener

import cn.hutool.core.net.NetUtil
import cn.hutool.core.util.URLUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.code.ErrorCodeRegister
import com.zyf.common.enums.SystemEnvironmentEnum
import com.zyf.common.utils.SmartEnumUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * 启动监听器
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021-12-23 23:45:26
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Component
@Order(value = 1024)
class WebServerListener : ApplicationListener<WebServerInitializedEvent> {
    @Value("\${reload.interval-seconds}")
    private val intervalSeconds: Int? = null

    override fun onApplicationEvent(webServerInitializedEvent: WebServerInitializedEvent) {
        val context: WebServerApplicationContext = webServerInitializedEvent.getApplicationContext()
        // 初始化reload
        initReload(context)
        // 项目信息
        showProjectMessage(webServerInitializedEvent)
    }

    /**
     * 显示项目信息
     */
    private fun showProjectMessage(webServerInitializedEvent: WebServerInitializedEvent) {
        val context: WebServerApplicationContext = webServerInitializedEvent.getApplicationContext()
        val env: Environment = context.getEnvironment()

        // 获取服务信息
        val ip = NetUtil.getLocalhost().hostAddress
        val port: Int = webServerInitializedEvent.getWebServer().getPort()
        var contextPath = env.getProperty("server.servlet.context-path")
        if (contextPath == null) {
            contextPath = ""
        }
        val profile = env.getProperty("spring.profiles.active")
        val environmentEnum: SystemEnvironmentEnum? = SmartEnumUtil.getEnumByValue(profile, SystemEnvironmentEnum::class)
        val projectName = env.getProperty("project.name")
        // 拼接服务地址
        val title = String.format("-------------【%s】 服务已成功启动 （%s started successfully）-------------", projectName, projectName)

        // 初始化状态码
        val codeCount: Int = ErrorCodeRegister.initialize()
        val localhostUrl = URLUtil.normalize(String.format("http://localhost:%d%s", port, contextPath), false, true)
        val externalUrl = URLUtil.normalize(String.format("http://%s:%d%s", ip, port, contextPath), false, true)
        val swaggerUrl = URLUtil.normalize(String.format("http://localhost:%d%s/swagger-ui/index.html", port, contextPath), false, true)
        val knife4jUrl = URLUtil.normalize(String.format("http://localhost:%d%s/doc.html", port, contextPath), false, true)
        log.warn(
            "\n{}\n" +
                    "\t当前启动环境:\t{} , {}" +
                    "\n\t返回码初始化:\t完成{}个返回码初始化" +
                    "\n\t服务本机地址:\t{}" +
                    "\n\t服务外网地址:\t{}" +
                    "\n\tSwagger地址:\t{}" +
                    "\n\tknife4j地址:\t{}" +
                    "\n-------------------------------------------------------------------------------------\n",
            title, profile, environmentEnum?.desc, codeCount, localhostUrl, externalUrl, swaggerUrl, knife4jUrl
        )
    }

    /**
     * 初始化reload
     */
    private fun initReload(applicationContext: WebServerApplicationContext) {
//        将applicationContext转换为ConfigurableApplicationContext
//        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
//
//
//        //获取BeanFactory
//        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
//
//        //动态注册bean
//        SmartReloadManager reloadManager = new SmartReloadManager(applicationContext.getBean(ReloadCommand.class), intervalSeconds);
//        defaultListableBeanFactory.registerSingleton("smartReloadManager", reloadManager);
    }
}