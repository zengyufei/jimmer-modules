package com.zyf.login.service

import cn.dev33.satoken.stp.StpInterface
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.extra.servlet.JakartaServletUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.code.UserErrorCode
import com.zyf.common.constant.RedisKeyConst
import com.zyf.common.constant.RequestHeaderConst
import com.zyf.common.constant.StringConst
import com.zyf.common.domain.RequestUser
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.*
import com.zyf.common.utils.SmartEnumUtil
import com.zyf.common.utils.SmartIpUtil
import com.zyf.employee.Employee
import com.zyf.employee.fetchBy
import com.zyf.employee.loginName
import com.zyf.login.domain.LoginForm
import com.zyf.login.domain.LoginResultVO
import com.zyf.login.domain.RequestEmployee
import com.zyf.login.domain.UserPermission
import com.zyf.loginLog.*
import com.zyf.repository.employee.DepartmentRepository
import com.zyf.repository.employee.EmployeeRepository
import com.zyf.repository.system.RoleRepository
import com.zyf.runtime.support.captcha.service.CaptchaService
import com.zyf.runtime.support.redis.RedisService
import com.zyf.service.dto.LoginLogVO
import com.zyf.service.dto.MenuVO
import com.zyf.service.dto.RoleVO
import com.zyf.support.service.*
import com.zyf.system.roleId
import com.zyf.system.service.RoleMenuService
import jakarta.servlet.http.HttpServletRequest
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentMap

@Slf4j
@Service
class LoginService(
    val sql: KSqlClient,
    val captchaService: CaptchaService,
    val objectMapper: ObjectMapper,
    val level3ProtectConfigService: Level3ProtectConfigService,
    val roleRepository: RoleRepository,
    val roleMenuService: RoleMenuService,
    val employeeRepository: EmployeeRepository,
    val departmentRepository: DepartmentRepository,
    val securityPasswordService: SecurityPasswordService,
    val apiEncryptService: ApiEncryptService,
    val configService: ConfigService,
    val redisService: RedisService,
    val securityLoginService: SecurityLoginService,
    val fileStorageService: IFileStorageService,
) : StpInterface {


    /**
     * 登录信息二级缓存
     */
    private val loginEmployeeCache: ConcurrentMap<String, RequestEmployee> = ConcurrentLinkedHashMap.Builder<String, RequestEmployee>().maximumWeightedCapacity(CACHE_MAX_ONLINE_PERSON_COUNT).build()


    /**
     * 权限 缓存
     */
    private val permissionCache: ConcurrentMap<String, UserPermission> = ConcurrentLinkedHashMap.Builder<String, UserPermission>().maximumWeightedCapacity(CACHE_MAX_ONLINE_PERSON_COUNT).build()

    companion object {
        /**
         * 万能密码的 sa token loginId 前缀
         */
        val SUPER_PASSWORD_LOGIN_ID_PREFIX: String = "S"

        /**
         * 最大在线缓存人数
         */
        val CACHE_MAX_ONLINE_PERSON_COUNT: Long = 1000L
    }


    /**
     * 员工登陆
     *
     * @return 返回用户登录信息
     */
    fun login(loginForm: LoginForm, ip: String?, userAgent: String?): ResponseDTO<LoginResultVO?> {
        val loginDeviceEnum: LoginDeviceEnum =
            SmartEnumUtil.getEnumByValue<LoginDeviceEnum>(loginForm.loginDevice)
                ?: return ResponseDTO.userErrorParam("登录设备暂不支持！")

        // 校验 图形验证码
        // val checkCaptcha: ResponseDTO<String?> = captchaService.checkCaptcha(loginForm)
        // if (!checkCaptcha.ok) {
        //     return ResponseDTO.error(UserErrorCode.PARAM_ERROR, checkCaptcha.msg)
        // }

        // 验证登录名
        val employee: Employee = sql.createQuery(Employee::class) {
            where(table.loginName eq loginForm.loginName)
            select(table.fetchBy {
                allScalarFields()
                department() {
                    departmentName()
                }
            })
        }.fetchOneOrNull()
            ?: return ResponseDTO.userErrorParam("登录名不存在！")

        // 验证账号状态
        if (employee.disabledFlag) {
            saveLoginLog(employee, ip, userAgent, "账号已禁用", LoginLogResultEnum.LOGIN_FAIL)
            return ResponseDTO.userErrorParam("您的账号已被禁用,请联系工作人员！")
        }

        // 解密前端加密的密码
        val requestPassword: String = apiEncryptService.decrypt(loginForm.password)

        // 验证密码 是否为万能密码
        val superPassword: String? = configService.getConfigValue(ConfigKeyEnum.SUPER_PASSWORD)
        val superPasswordFlag = superPassword == requestPassword

        // 校验双因子登录
        val validateEmailCode: ResponseDTO<String?> = validateEmailCode(loginForm, employee, superPasswordFlag)
        if (!validateEmailCode.ok) {
            return ResponseDTO.error(validateEmailCode)
        }

        // 万能密码特殊操作
        if (superPasswordFlag) {
            // 对于万能密码：受限制sa token 要求loginId唯一，万能密码只能插入一段uuid

            val saTokenLoginId: String =
                (SUPER_PASSWORD_LOGIN_ID_PREFIX + StringConst.COLON) + UUID.randomUUID()
                    .toString().replace("-", "") + StringConst.COLON + employee.employeeId
            // 万能密码登录只能登录30分钟
            StpUtil.login(saTokenLoginId, 1800)
        } else {
            // 按照等保登录要求，进行登录失败次数校验

            val loginFailEntityResponseDTO: ResponseDTO<LoginFail?> =
                securityLoginService.checkLogin(employee.employeeId, UserTypeEnum.ADMIN_EMPLOYEE)
            if (!loginFailEntityResponseDTO.ok) {
                return ResponseDTO.error(loginFailEntityResponseDTO)
            }

            // 密码错误
            if (employee.loginPwd != SecurityPasswordService.getEncryptPwd(requestPassword)) {
                // 记录登录失败
                saveLoginLog(employee, ip, userAgent, "密码错误", LoginLogResultEnum.LOGIN_FAIL)
                // 记录等级保护次数
                val msg: String? = securityLoginService.recordLoginFail(
                    employee.employeeId,
                    UserTypeEnum.ADMIN_EMPLOYEE,
                    employee.loginName,
                    loginFailEntityResponseDTO.data
                )
                return if (msg == null) ResponseDTO.userErrorParam("登录名或密码错误！") else ResponseDTO.error(
                    UserErrorCode.LOGIN_FAIL_WILL_LOCK,
                    msg
                )
            }

            val saTokenLoginId: String =
                UserTypeEnum.ADMIN_EMPLOYEE.value.toString() + StringConst.COLON + employee.employeeId

            // 登录
            StpUtil.login(saTokenLoginId, java.lang.String.valueOf(loginDeviceEnum.desc))

            // 移除邮箱验证码
            deleteEmailCode(employee.employeeId)
        }

        // 获取员工信息
        val requestEmployee: RequestEmployee = loadLoginInfo(employee)

        // 放入缓存
        loginEmployeeCache[employee.employeeId] = requestEmployee

        // 移除登录失败
        securityLoginService.removeLoginFail(employee.employeeId, UserTypeEnum.ADMIN_EMPLOYEE)

        // 获取登录结果信息
        val token: String = StpUtil.getTokenValue()
        val loginResultVO = getLoginResult(requestEmployee, token)

        // 保存登录记录
        saveLoginLog(
            employee,
            ip,
            userAgent,
            if (superPasswordFlag) "万能密码登录" else loginDeviceEnum.desc,
            LoginLogResultEnum.LOGIN_SUCCESS
        )

        // 设置 token
        loginResultVO.token = token

        // 清除权限缓存0
        permissionCache.remove(employee.employeeId)

        return ResponseDTO.ok(loginResultVO)
    }


    /**
     * 保存登录日志
     */
    private fun saveLoginLog(
        employee: Employee,
        ip: String?,
        inputUserAgent: String?,
        inputRemakr: String?,
        result: LoginLogResultEnum
    ) {
        val loginLog = LoginLog {
            userId = employee.employeeId
            userType = UserTypeEnum.ADMIN_EMPLOYEE.value
            userName = employee.actualName
            userAgent = inputUserAgent
            loginIp = ip
            loginIpRegion = SmartIpUtil.getRegion(ip)
            remark = inputRemakr
            loginResult = result.value
            createTime = LocalDateTime.now()
        }
        sql.insert(loginLog)
    }


    /**
     * 获取登录结果信息
     */
    fun getLoginResult(requestEmployee: RequestEmployee, token: String?): LoginResultVO {
        // 基础信息
        val loginResultVO = LoginResultVO()
        loginResultVO.userId = requestEmployee.userId
        loginResultVO.employeeId = requestEmployee.userId
        loginResultVO.userType = requestEmployee.userType
        loginResultVO.loginName = requestEmployee.loginName
        loginResultVO.userName = requestEmployee.userName
        loginResultVO.actualName = requestEmployee.actualName
        loginResultVO.avatar = requestEmployee.avatar
        loginResultVO.gender = requestEmployee.gender
        loginResultVO.phone = requestEmployee.phone
        loginResultVO.departmentId = requestEmployee.departmentId
        loginResultVO.departmentName = requestEmployee.departmentName
        loginResultVO.lastLoginIp = requestEmployee.ip
        loginResultVO.lastLoginUserAgent = requestEmployee.userAgent
        loginResultVO.disabledFlag = requestEmployee.disabledFlag
        loginResultVO.administratorFlag = requestEmployee.administratorFlag
        loginResultVO.remark = requestEmployee.remark
        loginResultVO.ip = requestEmployee.ip

        // 前端菜单和功能点清单
        val roleIds: List<String> = roleRepository.byEmployeeId(requestEmployee.userId) {
            select(table.roleId)
        }

        val menuAndPointsList: List<MenuVO> = roleMenuService.getMenuList(roleIds, requestEmployee.administratorFlag)
        loginResultVO.menuList = menuAndPointsList

        // 更新下后端权限缓存
        val userPermission: UserPermission = getUserPermission(requestEmployee.userId)
        permissionCache[requestEmployee.userId] = userPermission

        // 上次登录信息


        val loginLogVO: LoginLogVO? = sql.createQuery(LoginLog::class) {
            orderBy(table.loginLogId.desc())
            where(table.userId eq requestEmployee.userId)
            where(table.userType eq UserTypeEnum.ADMIN_EMPLOYEE.value)
            where(table.loginResult eq LoginLogResultEnum.LOGIN_SUCCESS.value)
            select(table.fetch(LoginLogVO::class))
        }.limit(1).fetchOneOrNull()
        if (loginLogVO != null) {
            loginResultVO.lastLoginIp = loginLogVO.loginIp
            loginResultVO.lastLoginIpRegion = loginLogVO.loginIpRegion
            loginResultVO.lastLoginTime = loginLogVO.createTime
            loginResultVO.lastLoginUserAgent = loginLogVO.userAgent
        }

        // 是否需要强制修改密码
        val needChangePasswordFlag: Boolean = securityPasswordService.checkNeedChangePassword(requestEmployee.userType.value, requestEmployee.userId)
        loginResultVO.needUpdatePwdFlag = needChangePasswordFlag

        // 万能密码登录，则不需要设置强制修改密码
        // val loginIdByToken = StpUtil.getLoginIdByToken(token)
        val loginIdByToken = StpUtil.getLoginIdByToken(token) as String
        if (loginIdByToken.startsWith(SUPER_PASSWORD_LOGIN_ID_PREFIX)) {
            loginResultVO.needUpdatePwdFlag = false
        }

        return loginResultVO
    }


    /**
     * 获取登录的用户信息
     */
    private fun loadLoginInfo(employee: Employee): RequestEmployee {
        // 基础信息

        val requestEmployee = RequestEmployee()
        requestEmployee.userId = employee.employeeId
        requestEmployee.employeeId = employee.employeeId
        requestEmployee.actualName = employee.actualName
        requestEmployee.userType = UserTypeEnum.ADMIN_EMPLOYEE
        requestEmployee.loginName = employee.loginName
        requestEmployee.userName = employee.actualName
        requestEmployee.avatar = employee.avatar
        requestEmployee.gender = employee.gender.value
        requestEmployee.phone = employee.phone
        requestEmployee.departmentId = employee.departmentId!!
        requestEmployee.departmentName = employee.department?.departmentName!!
        requestEmployee.disabledFlag = employee.disabledFlag
        requestEmployee.administratorFlag = employee.administratorFlag
        requestEmployee.remark = employee.remark

        // 头像信息
        val avatar: String? = employee.avatar
        if (!avatar.isNullOrBlank()) {
            val getFileUrl: ResponseDTO<String?> = fileStorageService.getFileUrl(avatar)
            if (getFileUrl.ok) {
                requestEmployee.avatar = getFileUrl.data
            }
        }

        return requestEmployee
    }


    /**
     * 根据登陆token 获取员请求工信息
     */
    fun getLoginEmployee(loginId: String?, request: HttpServletRequest?): RequestEmployee? {
        if (loginId == null) {
            return null
        }

        val requestEmployeeId = getEmployeeIdByLoginId(loginId) ?: return null

        var requestEmployee: RequestEmployee? = loginEmployeeCache[requestEmployeeId]
        if (requestEmployee == null) {
            // 员工基本信息
            val employeeEntity: Employee = employeeRepository.byId(requestEmployeeId) ?: return null

            requestEmployee = this.loadLoginInfo(employeeEntity)
            loginEmployeeCache[requestEmployeeId] = requestEmployee
        }


        // 更新请求ip和user agent
        requestEmployee.userAgent = JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT)
        requestEmployee.ip = JakartaServletUtil.getClientIP(request)

        return requestEmployee
    }

    /**
     * 根据 loginId 获取 员工id
     */
    fun getEmployeeIdByLoginId(loginId: String?): String? {
        if (loginId == null) {
            return null
        }

        try {
            // 如果是 万能密码 登录的用户
            val employeeIdStr: String = if (loginId.startsWith(SUPER_PASSWORD_LOGIN_ID_PREFIX)) {
                loginId.split(StringConst.COLON.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]
            } else {
                loginId.substring(2)
            }

            return employeeIdStr
        } catch (e: Exception) {
            log.error("loginId parse error , loginId : {}", loginId, e)
            return null
        }
    }


    /**
     * 退出登录
     */
    fun logout(token: String?, requestUser: RequestUser?): ResponseDTO<String?> {
        // sa token 登出

        StpUtil.logoutByTokenValue(token)

        if (requestUser == null) {
            return ResponseDTO.ok()
        }

        // 清空登录信息缓存
        loginEmployeeCache.remove(requestUser.userId)

        // 保存登出日志
        val loginLog = LoginLog {
            userId = requestUser.userId
            userType = requestUser.userType.value
            userName = requestUser.userName
            userAgent = requestUser.userAgent
            loginIp = requestUser.ip
            loginIpRegion = SmartIpUtil.getRegion(requestUser.ip)
            loginResult = LoginLogResultEnum.LOGIN_OUT.value
            createTime = LocalDateTime.now()
        }
        sql.insert(loginLog)
        return ResponseDTO.ok()
    }

    /**
     * 清除员工登录缓存
     */
    fun clearLoginEmployeeCache(employeeId: String?) {
        // 清空登录信息缓存
        loginEmployeeCache.remove(employeeId)
    }


    override fun getPermissionList(loginId: Any?, loginType: String?): MutableList<String> {
        val employeeId = this.getEmployeeIdByLoginId(loginId as String?) ?: return mutableListOf()

        var userPermission: UserPermission? = permissionCache[employeeId]
        if (userPermission == null) {
            userPermission = getUserPermission(employeeId)
            permissionCache[employeeId] = userPermission
        }

        return userPermission.permissionList.toMutableList()
    }

    override fun getRoleList(loginId: Any?, loginType: String?): List<String> {
        val employeeId = this.getEmployeeIdByLoginId(loginId as String?) ?: return emptyList()

        var userPermission: UserPermission? = permissionCache[employeeId]
        if (userPermission == null) {
            userPermission = getUserPermission(employeeId)
            permissionCache[employeeId] = userPermission
        }
        return userPermission.roleList.toList()
    }

    /**
     * 获取用户的权限（包含 角色列表、权限列表）
     */
    private fun getUserPermission(employeeId: String): UserPermission {
        val userPermission = UserPermission()

        // 角色列表
        val roleList: List<RoleVO> = roleRepository.byEmployeeId(employeeId) { select(table.fetch(RoleVO::class)) }
        userPermission.roleList.addAll(roleList.mapNotNull { it.roleCode }.toMutableSet())

        // 前端菜单和功能点清单
        val employee: Employee? = employeeRepository.byId(employeeId)

        val menuAndPointsList: List<MenuVO> = roleMenuService.getMenuList(roleList.map { it.roleId }.toList(), employee?.administratorFlag)

        // 权限列表
        val permissionSet = HashSet<String>()
        for (menu in menuAndPointsList) {
            if (menu.permsType == null) {
                continue
            }

            val perms: String? = menu.apiPerms
            if (perms.isNullOrBlank()) {
                continue
            }
            // 接口权限
            val split = perms.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            permissionSet.addAll(listOf(*split))
        }
        userPermission.permissionList.addAll(permissionSet)

        return userPermission
    }

    /**
     * 发送 邮箱 验证码
     */
    // fun sendEmailCode(loginName: String?): ResponseDTO<String?> {
    //     // 开启双因子登录
    //
    //     if (!level3ProtectConfigService.isTwoFactorLoginEnabled) {
    //         return ResponseDTO.userErrorParam("无需使用邮箱验证码")
    //     }
    //
    //     // 验证登录名
    //     val employee: Employee = loginName?.let { employeeRepository.byLoginName(it) } ?: return ResponseDTO.userErrorParam("登录名不存在！")
    //
    //     // 验证账号状态
    //     if (employee.disabledFlag) {
    //         return ResponseDTO.userErrorParam("您的账号已被禁用,请联系工作人员！")
    //     }
    //
    //     val mail: String? = employee.email
    //     if (mail != null && mail.isBlank()) {
    //         return ResponseDTO.userErrorParam("您暂未配置邮箱地址，请联系管理员配置邮箱")
    //     }
    //
    //     // 校验验证码发送时间，60秒内不能重复发生
    //     val redisVerificationCodeKey: String = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_EMPLOYEE.value + RedisKeyConst.SEPARATOR + employee.employeeId)
    //     val emailCode: String = redisService.get(redisVerificationCodeKey)
    //     var sendCodeTimeMills: Long = -1
    //     if (emailCode.isNotBlank()) {
    //         sendCodeTimeMills = NumberUtil.parseLong(emailCode.split(StringConst.UNDERLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().get(1))
    //     }
    //
    //     if (System.currentTimeMillis() - sendCodeTimeMills < 60 * 1000) {
    //         return ResponseDTO.userErrorParam("邮箱验证码已发送，一分钟内请勿重复发送")
    //     }
    //
    //     // 生成验证码
    //     val currentTimeMillis = System.currentTimeMillis()
    //     val verificationCode = RandomUtil.randomNumbers(4)
    //     redisService.set(redisVerificationCodeKey, verificationCode + StringConst.UNDERLINE + currentTimeMillis, 300)
    //
    //     // 发送邮件验证码
    //     val mailParams = HashMap<String, Any>()
    //     mailParams["code"] = verificationCode
    //     return mailService.sendMail(MailTemplateCodeEnum.LOGIN_VERIFICATION_CODE, mailParams, listOf(employee.email))
    // }


    /**
     * 校验邮箱验证码
     */
    private fun validateEmailCode(loginForm: LoginForm, employee: Employee, superPasswordFlag: Boolean): ResponseDTO<String?> {
        // 万能密码则不校验
        if (superPasswordFlag) {
            return ResponseDTO.ok()
        }

        // 未开启双因子登录
        if (!level3ProtectConfigService.isTwoFactorLoginEnabled) {
            return ResponseDTO.ok()
        }

        if (loginForm.emailCode.isNullOrBlank()) {
            return ResponseDTO.userErrorParam("请输入邮箱验证码")
        }

        // 校验验证码
        val redisVerificationCodeKey: String = redisService.generateRedisKey(
            RedisKeyConst.Support.LOGIN_VERIFICATION_CODE,
            UserTypeEnum.ADMIN_EMPLOYEE.value.toString() + RedisKeyConst.SEPARATOR + employee.employeeId
        )
        val emailCode: String? = redisService.get(redisVerificationCodeKey)
        if (emailCode.isNullOrBlank()) {
            return ResponseDTO.userErrorParam("邮箱验证码已失效，请重新发送")
        }

        if (emailCode.split(StringConst.UNDERLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] != loginForm.emailCode!!.trim()) {
            return ResponseDTO.userErrorParam("邮箱验证码错误，请重新填写")
        }

        return ResponseDTO.ok()
    }

    /**
     * 移除邮箱验证码
     */
    private fun deleteEmailCode(employeeId: String) {
        val redisVerificationCodeKey: String = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_EMPLOYEE.value.toString() + RedisKeyConst.SEPARATOR + employeeId)
        redisService.delete(redisVerificationCodeKey)
    }
}