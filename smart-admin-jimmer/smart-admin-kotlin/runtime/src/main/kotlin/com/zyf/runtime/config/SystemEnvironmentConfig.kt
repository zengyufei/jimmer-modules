package com.zyf.runtime.config

import com.zyf.common.base.SystemEnvironment
import com.zyf.common.enums.SystemEnvironmentEnum
import com.zyf.common.utils.SmartEnumUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * 系统环境
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021/08/13 18:56
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Configuration
class SystemEnvironmentConfig : Condition {

    @Value("\${spring.profiles.active}")
    lateinit var systemEnvironment: String

    @Value("\${project.name}")
    lateinit var projectName: String

    /**
     * 判断是否开启swagger
     */
    override fun matches(conditionContext: ConditionContext, annotatedTypeMetadata: AnnotatedTypeMetadata): Boolean {
        return isDevOrTest(conditionContext)
    }

    /**
     * 是否为：开发环境和 测试环境
     */
    private fun isDevOrTest(conditionContext: ConditionContext): Boolean {
        val property: String? = conditionContext.environment.getProperty("spring.profiles.active")
        return property?.isNotBlank() == true && (SystemEnvironmentEnum.TEST.equalsValue(property) || SystemEnvironmentEnum.DEV.equalsValue(
            property
        ))
    }

    @Bean("systemEnvironment")
    fun initEnvironment(): SystemEnvironment {
        val currentEnvironment: SystemEnvironmentEnum =
            SmartEnumUtil.getEnumByValue<SystemEnvironmentEnum>(systemEnvironment)
                ?: throw ExceptionInInitializerError("无法获取当前环境！请在 application.yaml 配置参数：spring.profiles.active")
        if (projectName.isBlank()) {
            throw ExceptionInInitializerError("无法获取当前项目名称！请在 application.yaml 配置参数：project.name")
        }
        return SystemEnvironment(currentEnvironment === SystemEnvironmentEnum.PROD, projectName, currentEnvironment)
    }
}
