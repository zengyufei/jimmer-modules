package com.zyf.support.service

import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSONUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.ConfigKeyEnum
import com.zyf.support.domain.Level3ProtectConfigForm
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

/**
 * 三级等保配置
 *
 * @Author 1024创新实验室-创始人兼主任:卓大
 * @Date 2024/7/30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net) ，Since 2012
 */
@Service
@Slf4j
class Level3ProtectConfigService {
    /**
     * 开启双因子登录，默认：开启
     */
    /**
     * 开启双因子登录，默认：开启
     */
    var isTwoFactorLoginEnabled: Boolean = false

    /**
     * 连续登录失败次数则锁定，-1表示不受限制，可以一直尝试登录
     */
    /**
     * 连续登录失败次数则锁定，-1表示不受限制，可以一直尝试登录
     */
    val loginFailMaxTimes: Int = -1

    /**
     * 连续登录失败锁定时间（单位：秒），-1表示不锁定，建议锁定30分钟
     */
    /**
     * 连续登录失败锁定时间（单位：秒），-1表示不锁定，建议锁定30分钟
     */
    var loginFailLockSeconds: Int = 1800

    /**
     * 最低活跃时间（单位：秒），超过此时间没有操作系统就会被冻结，默认-1 代表不限制，永不冻结; 默认 30分钟
     */
    /**
     * 最低活跃时间（单位：秒），超过此时间没有操作系统就会被冻结，默认-1 代表不限制，永不冻结; 默认 30分钟
     */
    var loginActiveTimeoutSeconds: Int = 1800

    /**
     * 密码复杂度 是否开启，默认：开启
     */
    /**
     * 密码复杂度 是否开启，默认：开启
     */
    var isPasswordComplexityEnabled: Boolean = true

    /**
     * 定期修改密码时间间隔（默认：天），默认：建议90天更换密码
     */
    /**
     * 定期修改密码时间间隔（默认：天），默认：建议90天更换密码
     */
    var regularChangePasswordDays: Long = 90

    /**
     * 定期修改密码不允许相同次数，默认：3次以内密码不能相同
     */
    /**
     * 定期修改密码不允许相同次数，默认：3次以内密码不能相同
     */
    var regularChangePasswordNotAllowRepeatTimes: Int = 3

    /**
     * 文件大小限制，单位 mb ，(默认：50 mb)
     */
    /**
     * 文件大小限制，单位 mb ，(默认：50 mb)
     */
    var maxUploadFileSizeMb: Long = 50

    /**
     * 文件检测，默认：不开启
     */
    /**
     * 文件检测，默认：不开启
     */
    var isFileDetectFlag: Boolean = false


    @Resource
    private val configService: ConfigService? = null

    @PostConstruct
    fun init() {
        val configValue = configService!!.getConfigValue(ConfigKeyEnum.LEVEL3_PROTECT_CONFIG)
        if (StrUtil.isEmpty(configValue)) {
            throw ExceptionInInitializerError("t_config 表 三级等保配置为空，请进行配置！")
        }
        val level3ProtectConfigForm = JSONUtil.toBean(configValue, Level3ProtectConfigForm::class.java)
        setProp(level3ProtectConfigForm)
    }

    /**
     * 设置属性
     */
    private fun setProp(configForm: Level3ProtectConfigForm) {
        if (configForm.fileDetectFlag != null) {
            this.isFileDetectFlag = configForm.fileDetectFlag!!
        }

        if (configForm.maxUploadFileSizeMb != null) {
            this.maxUploadFileSizeMb = configForm.maxUploadFileSizeMb!!
        }

        if (configForm.loginFailLockMinutes != null) {
            this.loginFailLockSeconds = configForm.loginFailLockMinutes!! * 60
        }

        if (configForm.loginActiveTimeoutMinutes != null) {
            this.loginActiveTimeoutSeconds = configForm.loginActiveTimeoutMinutes!! * 60
        }

        if (configForm.passwordComplexityEnabled != null) {
            this.isPasswordComplexityEnabled = configForm.passwordComplexityEnabled!!
        }

        if (configForm.regularChangePasswordMonths != null) {
            this.regularChangePasswordDays = configForm.regularChangePasswordMonths!! * 30
        }

        if (configForm.twoFactorLoginEnabled != null) {
            this.isTwoFactorLoginEnabled = configForm.twoFactorLoginEnabled!!
        }

        if (configForm.regularChangePasswordNotAllowRepeatTimes != null) {
            this.regularChangePasswordNotAllowRepeatTimes = configForm.regularChangePasswordNotAllowRepeatTimes!!
        }

        // 设置 最低活跃时间（单位：秒）
        // if (this.loginActiveTimeoutSeconds > 0) {
        //     StpUtil.getStpLogic().getConfigOrGlobal().setActiveTimeout(getLoginActiveTimeoutSeconds());
        // } else {
        //     StpUtil.getStpLogic().getConfigOrGlobal().setActiveTimeout(-1);
        // }
    }

    /**
     * 更新三级等保配置
     */
    fun updateLevel3Config(configForm: Level3ProtectConfigForm): ResponseDTO<String?> {
        // 设置属性
        setProp(configForm)
        // 保存数据库
        val configFormJsonString = JSONUtil.toJsonStr(configForm)
        return configService!!.updateValueByKey(ConfigKeyEnum.LEVEL3_PROTECT_CONFIG, configFormJsonString)
    }
}
