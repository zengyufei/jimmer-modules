package com.zyf.support.domain

import jakarta.validation.constraints.NotNull

/**
 * 三级等保相关配置
 *
 * @Author 1024创新实验室-创始人兼主任:卓大
 * @Date 2024/7/30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net) ，Since 2012
 */
class Level3ProtectConfigForm {
    /** 连续登录失败次数则锁定  */
    var loginFailMaxTimes: @NotNull(message = "连续登录失败次数则锁定 不能为空") Int? = null

    /** 连续登录失败锁定时间（单位：分钟）  */
    var loginFailLockMinutes: @NotNull(message = "连续登录失败锁定时间（单位：分钟） 不能为空") Int? = null

    /** 最低活跃时间（单位：分钟）  */
    var loginActiveTimeoutMinutes: @NotNull(message = "最低活跃时间（单位：分钟） 不能为空") Int? = null

    /** 开启双因子登录  */
    var twoFactorLoginEnabled: @NotNull(message = "开启双因子登录 不能为空") Boolean? = null

    /** 密码复杂度 是否开启，默认：开启  */
    var passwordComplexityEnabled: @NotNull(message = "密码复杂度 是否开启 不能为空") Boolean? = null

    /** 定期修改密码时间间隔（默认：月）  */
    var regularChangePasswordMonths: @NotNull(message = "定期修改密码时间间隔（默认：月） 不能为空") Long? = null

    /** 定期修改密码不允许重复次数，默认：3次以内密码不能相同（默认：次）  */
    var regularChangePasswordNotAllowRepeatTimes: @NotNull(message = "定期修改密码不允许重复次数 不能为空") Int? = null

    /** 文件检测，默认：不开启  */
    var fileDetectFlag: @NotNull(message = "文件检测 是否开启 不能为空") Boolean? = null

    /** 文件大小限制，单位 mb ，(默认：50 mb)  */
    var maxUploadFileSizeMb: @NotNull(message = "文件大小限制  不能为空") Long? = null
}
