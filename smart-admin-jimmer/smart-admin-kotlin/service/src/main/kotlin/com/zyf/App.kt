package com.zyf

import com.zyf.cfg.listener.Ip2RegionListener
import com.zyf.cfg.listener.LogVariableListener
import org.babyfish.jimmer.client.EnableImplicitApi
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableImplicitApi
// @EnableAspectJAutoProxy
class App

fun main(args: Array<String>) {

	val application = SpringApplication(App::class.java)
	// 添加 日志监听器，使 log4j2-spring.xml 可以间接读取到配置文件的属性
	application.addListeners(LogVariableListener(), Ip2RegionListener())
	application.run(*args)
}
