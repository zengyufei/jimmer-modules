package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.domain.RequestUser
import com.zyf.loginLog.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.RandomStringUtils
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * 三级等保 密码 相关
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/11 19:25:59
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Service
class SecurityPasswordService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val level3ProtectConfigService: Level3ProtectConfigService
) {


    /**
     * 校验密码复杂度
     */
    fun validatePasswordComplexity(password: String): ResponseDTO<String?> {
        if (password.isEmpty()) {
            return ResponseDTO.userErrorParam(PASSWORD_FORMAT_MSG)
        }

        // 密码长度必须大于等于8位
        if (password.length < PASSWORD_LENGTH) {
            return ResponseDTO.userErrorParam(PASSWORD_FORMAT_MSG)
        }

        // 无需校验 密码复杂度
        if (!level3ProtectConfigService!!.isPasswordComplexityEnabled) {
            return ResponseDTO.ok()
        }

        if (!password.matches(PASSWORD_PATTERN.toRegex())) {
            return ResponseDTO.userErrorParam(PASSWORD_FORMAT_MSG)
        }

        return ResponseDTO.ok()
    }

    /**
     * 校验密码重复次数
     */
    fun validatePasswordRepeatTimes(requestUser: RequestUser, newPassword: String?): ResponseDTO<String?> {
        // 密码重复次数小于1  无需校验

        if (level3ProtectConfigService.regularChangePasswordNotAllowRepeatTimes < 1) {
            return ResponseDTO.ok()
        }

        // 检查最近几次是否有重复密码
        val oldPasswords: List<String?> = sql.createQuery(PasswordLog::class) {
            orderBy(table.passwordLogId.desc())
            requestUser.userType?.let {
                where(table.userType eq it.value)
            }
            requestUser.userId?.let {
                where(table.employeeId eq it)
            }
            select(table.newPassword)
        }.limit(level3ProtectConfigService.regularChangePasswordNotAllowRepeatTimes).execute()


        if (oldPasswords.contains(getEncryptPwd(newPassword))) {
            return ResponseDTO.userErrorParam(String.format("与前%s个历史密码重复，请换个密码!", level3ProtectConfigService.regularChangePasswordNotAllowRepeatTimes))
        }

        return ResponseDTO.ok()
    }

    /**
     * 随机生成密码
     */
    fun randomPassword(): String {
        // 未开启密码复杂度，则由8为数字构成
        if (!level3ProtectConfigService.isPasswordComplexityEnabled) {
            return RandomStringUtils.randomNumeric(PASSWORD_LENGTH)
        }

        // 3位大写字母，2位数字，2位小写字母 + 1位特殊符号
        return (RandomStringUtils.randomAlphabetic(3).uppercase(Locale.getDefault())
                + RandomStringUtils.randomNumeric(2)
                + RandomStringUtils.randomAlphabetic(2).lowercase(Locale.getDefault())
                + (if (ThreadLocalRandom.current().nextBoolean()) "#" else "@"))
    }


    /**
     * 保存修改密码
     */
    fun saveUserChangePasswordLog(requestUser: RequestUser, inputNewPassword: String?, inputOldPassword: String?) {
        val passwordLog = PasswordLog {
            newPassword = inputNewPassword
            inputOldPassword?.let { oldPassword = it }
            employeeId = requestUser.userId
            userType = requestUser.userType!!.value
        }
        sql.insert(passwordLog)
    }

    /**
     * 检查是否需要修改密码
     */
    fun checkNeedChangePassword(userType: Int?, userId: String?): Boolean {
        if (level3ProtectConfigService.regularChangePasswordDays < 1) {
            return false
        }

        val passwordLog: PasswordLog = sql.createQuery(PasswordLog::class) {
            orderBy(table.passwordLogId.desc())
            userType?.let {
                where(table.userType eq it)
            }
            userId?.let {
                where(table.employeeId eq it)
            }
            select(table)
        }.limit(1).fetchOneOrNull() ?: return false

        val nextUpdateTime: LocalDateTime = passwordLog.createTime.plusDays(level3ProtectConfigService.regularChangePasswordDays)
        return nextUpdateTime.isBefore(LocalDateTime.now())
    }

    companion object {
        /**
         * 密码长度8-20位且包含大小写字母、数字、特殊符号三种及以上组合
         */
        const val PASSWORD_PATTERN: String = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_!@#$%^&*`~()-+=]+$)(?![a-z0-9]+$)(?![a-z\\W_!@#$%^&*`~()-+=]+$)(?![0-9\\W_!@#$%^&*`~()-+=]+$)[a-zA-Z0-9\\W_!@#$%^&*`~()-+=]*$"

        const val PASSWORD_FORMAT_MSG: String = "密码必须为长度8-20位且必须包含大小写字母、数字、特殊符号（如：@#$%^&*()_+-=）等三种字符"


        const val PASSWORD_LENGTH: Int = 8

        const val PASSWORD_SALT_FORMAT: String = "smart_%s_admin_$^&*"


        /**
         * 获取 加密后 的密码
         */
        fun getEncryptPwd(password: String?): String {
            return DigestUtils.md5Hex(String.format(PASSWORD_SALT_FORMAT, password))
        }
    }
}
