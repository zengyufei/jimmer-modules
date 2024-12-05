package com.zyf.runtime.support.captcha.service.config

import java.awt.Color
import java.util.*

/**
 * 验证码颜色
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
object CaptchaColor {
    val color: Color
        get() {
            val colors: MutableList<Color> = mutableListOf()
            colors.add(Color(0, 135, 255))
            colors.add(Color(51, 153, 51))
            colors.add(Color(255, 102, 102))
            colors.add(Color(255, 153, 0))
            colors.add(Color(153, 102, 0))
            colors.add(Color(153, 102, 153))
            colors.add(Color(51, 153, 153))
            colors.add(Color(102, 102, 255))
            colors.add(Color(0, 102, 204))
            colors.add(Color(204, 51, 51))
            colors.add(Color(128, 153, 65))
            val random = Random()
            val colorIndex = random.nextInt(10)
            return colors[colorIndex]
        }
}
