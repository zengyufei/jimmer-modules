package com.zyf.cfg

import com.zyf.runtime.config.SystemEnvironmentConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Conditional(SystemEnvironmentConfig::class)
class CorsConfig : WebMvcConfigurer {

    @Value("\${project.access-control-allow-origin}")
    lateinit var accessControlAllowOrigin: String

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        // 设置访问源地址
        config.addAllowedOriginPattern(accessControlAllowOrigin)
        // 设置访问源请求头
        config.addAllowedHeader("*")
        // 设置访问源请求方法
        config.addAllowedMethod("*")
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

}