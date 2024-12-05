package com.zyf.runtime.support.captcha.service.domain

class CaptchaVO {

    /** 验证码唯一标识 */
    var captchaUuid: String? = null

    /** 验证码图片内容-生产环境无效 */
    var captchaText: String? = null

    /** 验证码Base64图片 */
    var captchaBase64Image: String? = null

    /** 过期时间（秒） */
    var expireSeconds: Long? = null

}
