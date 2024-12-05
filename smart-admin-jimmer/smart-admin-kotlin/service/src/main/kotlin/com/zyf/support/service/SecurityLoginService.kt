package com.zyf.support.service

import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.util.DesensitizedUtil.userId
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.loginLog.*
import com.zyf.oa.Enterprise
import com.zyf.oa.createTime
import com.zyf.runtime.support.captcha.service.CaptchaService
import com.zyf.service.dto.EnterpriseVO
import com.zyf.service.dto.LoginFailQueryForm
import com.zyf.service.dto.LoginFailVO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 三级等保 登录 相关
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/11 19:25:59
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */
@Service
class SecurityLoginService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val level3ProtectConfigService: Level3ProtectConfigService,
) {

    companion object {
        private const val LOGIN_LOCK_MSG = "您已连续登录失败%s次，账号锁定%s分钟，解锁时间为：%s，请您耐心等待！"
        private const val LOGIN_FAIL_MSG = "登录名或密码错误！连续登录失败%s次，账号将锁定%s分钟！您还可以再尝试%s次！"
    }


    /**
     * 检查是否可以登录
     */
    fun checkLogin(userId: String, userType: UserTypeEnum): ResponseDTO<LoginFail?> {
        // 若登录最大失败次数小于1，无需校验
        if (level3ProtectConfigService.loginFailMaxTimes < 1) {
            return ResponseDTO.ok()
        }

        val loginFail = sql.createQuery(LoginFail::class) {
            where(table.employeeId eq userId)
            where(table.userType eq userType.value)
            select(table)
        }.fetchOneOrNull() ?: return ResponseDTO.ok()

        // 校验登录失败次数
        if (loginFail.loginFailCount!! < level3ProtectConfigService.loginFailMaxTimes) {
            return ResponseDTO.ok(loginFail)
        }

        // 校验是否锁定
        if (loginFail.loginLockBeginTime == null) {
            return ResponseDTO.ok(loginFail)
        }

        // 校验锁定时长
        if (loginFail.loginLockBeginTime!!.plusSeconds(level3ProtectConfigService.loginFailLockSeconds.toLong())
                .isBefore(LocalDateTime.now())
        ) {
            // 过了锁定时间
            return ResponseDTO.ok(loginFail)
        }

        val unlockTime = loginFail.loginLockBeginTime!!.plusSeconds(level3ProtectConfigService.loginFailLockSeconds.toLong())
        return ResponseDTO.error(
            UserErrorCode.LOGIN_FAIL_LOCK,
            String.format(
                LOGIN_LOCK_MSG,
                loginFail.loginFailCount,
                level3ProtectConfigService.loginFailLockSeconds / 60,
                LocalDateTimeUtil.formatNormal(unlockTime)
            )
        )
    }

    /**
     * 登录失败后记录
     */
    fun recordLoginFail(userId: String, userType: UserTypeEnum, loginName: String, loginFail: LoginFail?): String? {
        // 若登录最大失败次数小于1，无需记录
        if (level3ProtectConfigService.loginFailMaxTimes < 1) {
            return null
        }

        // 登录失败
        val loginFailCount = if (loginFail == null) 1 else loginFail.loginFailCount!! + 1
        val lockFlag = loginFailCount >= level3ProtectConfigService.loginFailMaxTimes
        val lockBeginTime = if (lockFlag) LocalDateTime.now() else null

        if (loginFail == null) {
            val newLoginFailEntity = LoginFail {
                this.employeeId = userId
                this.userType = userType.value
               this.loginName = loginName
               this.loginFailCount = loginFailCount
                this.lockFlag = lockFlag
                this.loginLockBeginTime = lockBeginTime
            }
            sql.insert(newLoginFailEntity)
        } else {
            val newLoginFail = loginFail.copy {
                this.loginLockBeginTime = lockBeginTime
                this.loginFailCount = loginFailCount
                this.lockFlag = lockFlag
                this.loginName = loginName
            }
            sql.update(newLoginFail)
        }

        // 提示信息
        return if (lockFlag) {
            val unlockTime = (loginFail?.loginLockBeginTime ?: lockBeginTime)!!.plusSeconds(level3ProtectConfigService.loginFailLockSeconds.toLong())
            String.format(
                LOGIN_LOCK_MSG,
                loginFailCount,
                level3ProtectConfigService.loginFailLockSeconds / 60,
                LocalDateTimeUtil.formatNormal(unlockTime)
            )
        } else {
            String.format(
                LOGIN_FAIL_MSG,
                level3ProtectConfigService.loginFailMaxTimes,
                level3ProtectConfigService.loginFailLockSeconds / 60,
                level3ProtectConfigService.loginFailMaxTimes - loginFailCount
            )
        }
    }

    /**
     * 清除登录失败
     */
    fun removeLoginFail(userId: String, userType: UserTypeEnum) {
        // 若登录最大失败次数小于1，无需校验
        if (level3ProtectConfigService.loginFailMaxTimes < 1) {
            return
        }
        sql.createDelete(LoginFail::class) {
            where(table.employeeId eq userId)
            where(table.userType eq userType.value)
        }.execute()
    }

    /**
     * 分页查询
     */
    fun queryPage(
        pageBean: PageBean,
        queryForm: LoginFailQueryForm
    ): PageResult<LoginFailVO> {
        val pageResult = sql.createQuery(LoginFail::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.updateTime.desc())

            where(queryForm)
            select(table.fetch(LoginFailVO::class))
        }.page(pageBean)
        return pageResult
    }

    /**
     * 批量删除
     */
    fun batchDelete(idList: List<String>): ResponseDTO<String?> {
        if (idList.isEmpty()) {
            return ResponseDTO.ok()
        }

        sql.deleteByIds(LoginFail::class, idList)
        return ResponseDTO.ok()
    }
}