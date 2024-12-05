package com.zyf.runtime.support.captcha.service.config

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class CaptchaConfig {


    @Bean
    fun getDefaultKaptcha(): DefaultKaptcha {
        val defaultKaptcha: DefaultKaptcha = DefaultKaptcha()
        val properties: Properties = Properties()
        properties.setProperty("kaptcha.border", "no")
        properties.setProperty("kaptcha.border.color", "34,114,200")
        properties.setProperty("kaptcha.image.width", "125")
        properties.setProperty("kaptcha.image.height", "45")
        properties.setProperty("kaptcha.textproducer.char.string", "123456789")
        properties.setProperty("kaptcha.textproducer.char.length", "4")
        properties.setProperty(
            "kaptcha.textproducer.font.names",
            "Arial,Arial Narrow,Serif,Helvetica,Tahoma,Times New Roman,Verdana"
        )
        properties.setProperty("kaptcha.textproducer.font.size", "38")

        properties.setProperty("kaptcha.background.clear.from", "white")
        properties.setProperty("kaptcha.background.clear.to", "white")

        properties.setProperty("kaptcha.word.impl", CaptchaWordRenderer::class.java.getName())
        properties.setProperty("kaptcha.noise.impl", CaptchaNoise::class.java.getName())

        val config: Config = Config(properties)
        defaultKaptcha.setConfig(config)
        return defaultKaptcha
    }

}