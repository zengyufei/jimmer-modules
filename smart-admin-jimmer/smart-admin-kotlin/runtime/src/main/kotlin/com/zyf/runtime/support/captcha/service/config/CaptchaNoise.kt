package com.zyf.runtime.support.captcha.service.config

import com.google.code.kaptcha.NoiseProducer
import com.google.code.kaptcha.util.Configurable
import com.zyf.runtime.support.captcha.service.config.CaptchaColor.color
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.util.*

/**
 * 验证码加噪处理
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class CaptchaNoise : Configurable(), NoiseProducer {
    override fun makeNoise(
        image: BufferedImage,
        factorOne: Float,
        factorTwo: Float,
        factorThree: Float,
        factorFour: Float
    ) {
        val width = image.width
        val height = image.height
        val graph = image.graphics as Graphics2D
        graph.setRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))
        graph.stroke = BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)
        val random = Random()
        var noiseLineNum = random.nextInt(3)
        if (noiseLineNum == 0) {
            noiseLineNum = 1
        }
        for (i in 0 until noiseLineNum) {
            graph.color = color
            graph.drawLine(
                random.nextInt(width),
                random.nextInt(height),
                10 + random.nextInt(20),
                10 + random.nextInt(20)
            )
        }

        graph.dispose()
    }
}
