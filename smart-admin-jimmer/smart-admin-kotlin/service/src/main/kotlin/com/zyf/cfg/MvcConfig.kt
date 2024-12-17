package com.zyf.cfg

import com.zyf.cfg.interceptor.AdminInterceptor
import com.zyf.runtime.convert.IntEnumConvertorFactory
import com.zyf.runtime.convert.StringEnumConvertorFactory
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * web相关配置
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Configuration
class MvcConfig(val adminInterceptor: AdminInterceptor) : WebMvcConfigurer {

    val SWAGGER_WHITELIST = mutableListOf(
        "/openapi.yml",
        "/openapi.html",
        "/favicon**",
        "/ts.zip",
    )

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(adminInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(SWAGGER_WHITELIST)
    }

    /**
     * 枚举类的转换器工厂 addConverterFactory
     */
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(IntEnumConvertorFactory())
        registry.addConverterFactory(StringEnumConvertorFactory())
    }
}
