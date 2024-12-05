package com.zyf.runtime.support.captcha.service

import cn.hutool.core.util.StrUtil
import com.google.code.kaptcha.impl.DefaultKaptcha
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.base.SystemEnvironment
import com.zyf.common.constant.RedisKeyConst
import com.zyf.common.constant.StringConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.runtime.support.captcha.service.domain.CaptchaForm
import com.zyf.runtime.support.captcha.service.domain.CaptchaVO
import com.zyf.runtime.support.redis.RedisService
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * 过期时间：65秒
 */
val EXPIRE_SECOND: Long = 65L

@Slf4j
@Service
class CaptchaService(
    val defaultKaptcha: DefaultKaptcha,
    val redisService: RedisService,
    val systemEnvironment: SystemEnvironment
) {


    /**
     * 生成图形验证码
     * 默认 1 分钟有效期
     *
     */
    fun generateCaptcha(): CaptchaVO {
        val captchaText: String = defaultKaptcha.createText()
        val image: BufferedImage = defaultKaptcha.createImage(captchaText)

        var base64Code: String
        try {
            ByteArrayOutputStream().use { os ->
                ImageIO.write(image, "jpg", os)
                base64Code = Base64Utils.encodeToString(os.toByteArray())
            }
        } catch (e: Exception) {
            log.error("generateCaptcha error:", e)
            throw RuntimeException("生成验证码错误")
        }

        /*
         * 返回验证码对象
         * 图片 base64格式
         */
        // uuid 唯一标识
        val uuid = UUID.randomUUID().toString().replace("-", StringConst.EMPTY)

        val captchaVO = CaptchaVO()
        captchaVO.captchaUuid = uuid
        captchaVO.captchaText = captchaText
        captchaVO.captchaBase64Image = "data:image/png;base64,$base64Code"

        captchaVO.expireSeconds = EXPIRE_SECOND
        if (!systemEnvironment.isProd) {
            captchaVO.captchaText = captchaText
        }
        val redisCaptchaKey: String = redisService.generateRedisKey(RedisKeyConst.Support.CAPTCHA, uuid)
        redisService.set(redisCaptchaKey, captchaText, EXPIRE_SECOND)
        return captchaVO
    }


    /**
     * 校验图形验证码
     *
     */
    fun checkCaptcha(captchaForm: CaptchaForm): ResponseDTO<String?> {
        if (StrUtil.isBlank(captchaForm.captchaUuid) || StrUtil.isBlank(
                captchaForm.captchaCode
            )
        ) {
            return ResponseDTO.userErrorParam("请输入正确验证码")
        }
        /*
         * 1、校验redis里的验证码
         * 2、校验成功后，删除redis
         */
        val redisCaptchaKey = redisService.generateRedisKey(RedisKeyConst.Support.CAPTCHA, captchaForm.captchaUuid)
        val redisCaptchaCode = redisService.get(redisCaptchaKey)
        if (StrUtil.isBlank(redisCaptchaCode)) {
            return ResponseDTO.userErrorParam("验证码已过期，请刷新重试")
        }
        if (redisCaptchaCode != captchaForm.captchaCode) {
            return ResponseDTO.userErrorParam("验证码错误，请输入正确的验证码")
        }
        // 删除已使用的验证码
        redisService.delete(redisCaptchaKey)
        return ResponseDTO.ok()
    }

}