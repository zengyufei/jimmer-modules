package com.zyf.common.constant

/**
 * redis key 常量类
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-05-30 21:22:12
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
object RedisKeyConst {
    const val SEPARATOR: String = ":"

    object Support {
        const val FILE_PRIVATE_VO: String = "file:private:"

        const val SERIAL_NUMBER_LAST_INFO: String = "serial-number:last-info"

        const val SERIAL_NUMBER: String = "serial-number:"

        const val CAPTCHA: String = "captcha:"

        const val LOGIN_VERIFICATION_CODE: String = "login:verification-code:"
    }
}
