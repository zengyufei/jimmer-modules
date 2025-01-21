package com.zyf.cfg

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.cfg.interceptor.AdminInterceptor
import com.zyf.runtime.convert.IntEnumConvertorFactory
import com.zyf.runtime.convert.StringEnumConvertorFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.method.support.HandlerMethodArgumentResolver
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
class MvcConfig(
    val adminInterceptor: AdminInterceptor,

    @Value("\${project.pageField.enabled}")
    private val pageFieldEnabled: Boolean,
    @Value("\${project.pageField.pageNum}")
    private val pageFieldPageNum: String,
    @Value("\${project.pageField.pageSize}")
    private val pageFieldPageSize: String,
    @Value("\${project.pageField.sortCode}")
    private val pageFieldSortCode: String,
    val objectMapper: ObjectMapper
) : WebMvcConfigurer {

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


    @Bean
    @ConditionalOnProperty(name = ["project.pageField.enabled"], havingValue = "true")
    fun getRequestBodyFilter(): FilterRegistrationBean<RequestBodyFilter> {
        val registrationBean: FilterRegistrationBean<RequestBodyFilter> = FilterRegistrationBean<RequestBodyFilter>()
        registrationBean.filter = RequestBodyFilter()
        registrationBean.addUrlPatterns("/*") // 设置过滤器路径
        registrationBean.order = 10
        return registrationBean
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        if (pageFieldEnabled) {
            resolvers.add(PageBeanArgumentResolver(pageFieldPageNum, pageFieldPageSize, pageFieldSortCode, objectMapper))
        }
    }
}
