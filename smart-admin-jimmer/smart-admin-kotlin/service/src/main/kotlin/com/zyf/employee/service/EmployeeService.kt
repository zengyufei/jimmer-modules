package com.zyf.employee.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.RequestUser
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.department.service.DepartmentService
import com.zyf.employee.*
import com.zyf.login.service.LoginService
import com.zyf.repository.employee.DepartmentRepository
import com.zyf.repository.employee.EmployeeRepository
import com.zyf.service.dto.*
import com.zyf.support.service.SecurityPasswordService
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.ast.expression.`valueIn?`
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class EmployeeService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val departmentService: DepartmentService,
    val employeeRepository: EmployeeRepository,
    val departmentRepository: DepartmentRepository,
    val securityPasswordService: SecurityPasswordService,
    private val loginService: LoginService,
) {

    fun getById(employeeId: String): Employee? {
        return employeeRepository.byId(employeeId)
    }

    /**
     * 查询员工列表
     */
    fun queryEmployee(pageBean: PageBean, employeeQueryForm: EmployeeQueryForm): ResponseDTO<PageResult<EmployeeVO>> {

        val departmentIdList = mutableListOf<String>()
        employeeQueryForm.departmentId?.let {
            departmentIdList.addAll(departmentService.selfAndChildrenIdList(it))
        }


        val pageResult = sql.createQuery(Employee::class) {
            orderBy(pageBean)
            where(
                employeeQueryForm.copy(
                    departmentId = null
                )
            )
            departmentIdList.takeIf { it.isNotEmpty() }?.let {
                where(table.departmentId `valueIn?` departmentIdList)
            }
            select(table.fetch(EmployeeVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 新增员工
     */
    @Synchronized
    fun addEmployee(employeeAddForm: EmployeeAddForm): ResponseDTO<String?> {
        // 校验登录名是否重复
        getByLoginName(employeeAddForm.loginName)?.let {
            return ResponseDTO.userErrorParam("登录名重复")
        }

        // 校验电话是否存在
        employeeAddForm.phone?.let {
            if (employeeRepository.existsPhone(it)) {
                return ResponseDTO.userErrorParam("手机号已存在")
            }
        }

        // 部门是否存在
        employeeAddForm.departmentId?.let {
            if (!departmentRepository.existsId(it)) {
                return ResponseDTO.userErrorParam("部门不存在")
            }
        }

        // 设置密码 默认密码
        val password = securityPasswordService.randomPassword()
        val employee = employeeAddForm.toEntity {
            loginPwd = SecurityPasswordService.getEncryptPwd(password)
            administratorFlag = false
        }

        // 保存数据
        sql.save(employee)

        return ResponseDTO.ok(password)
    }


    // 更新员工
    @Synchronized
    fun updateEmployee(employeeUpdateForm: EmployeeUpdateForm): ResponseDTO<String?> {
        val employeeId = employeeUpdateForm.employeeId ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        val departmentId = employeeUpdateForm.departmentId ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        if (employeeRepository.notExistsId(employeeId)) return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        // 部门是否存在
        if (departmentRepository.notExistsId(departmentId)) return ResponseDTO.userErrorParam("部门不存在")

        val existEntity = employeeRepository.existsBy() {
            where(table.loginName eq employeeUpdateForm.loginName)
            where(table.employeeId ne employeeId)
        }
        if (existEntity) {
            return ResponseDTO.userErrorParam("登录名重复")
        }

        employeeUpdateForm.phone?.let { p ->
            if (employeeRepository.existsBy() {
                    where(table.phone eq employeeUpdateForm.phone)
                    where(table.employeeId ne employeeId)
                }) {
                return ResponseDTO.userErrorParam("手机号已存在")
            }
        }


        // 更新数据
        // 保存员工 获得id
        sql.update(employeeUpdateForm);

        // 清除员工缓存
        loginService.clearLoginEmployeeCache(employeeId)

        return ResponseDTO.ok()
    }

    // 更新头像
    fun updateAvatar(employeeUpdateAvatarForm: EmployeeUpdateAvatarForm): ResponseDTO<String?> {
        val inputEmployeeId = employeeUpdateAvatarForm.employeeId ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        if (employeeRepository.notExistsId(inputEmployeeId)) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        }

        // 更新头像
        val updateEntity = Employee {
            employeeId = inputEmployeeId
            avatar = employeeUpdateAvatarForm.avatar
        }
        sql.update(updateEntity)

        // 清除员工缓存
        loginService.clearLoginEmployeeCache(inputEmployeeId)
        return ResponseDTO.ok()
    }

    /**
     * 更新员工的禁用标志
     *
     * 此函数旨在更新数据库中指定员工的禁用状态如果员工ID为空或在数据库中找不到对应的员工，
     * 则返回错误响应否则，它将更新员工的禁用标志为false，并在员工之前已被禁用的情况下强制其退出登录
     *
     * @param inputEmployeeId 可能为null的员工ID
     * @return ResponseDTO<String?> 更新操作的响应对象，包含结果或错误信息
     */
    fun updateDisableFlag(inputEmployeeId: String?): ResponseDTO<String?> {
        // 检查输入的员工ID是否为空，如果为空，则返回错误响应
        if (inputEmployeeId == null) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        }
        // 尝试从数据库中获取员工信息，如果不存在，则返回错误响应
        val employee = employeeRepository.byId(inputEmployeeId)
            ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        val oldDisabledFlag = employee.disabledFlag
        // 准备更新实体，设置员工ID和禁用标志为false
        val updateEntity = Employee {
            employeeId = inputEmployeeId
            disabledFlag = !oldDisabledFlag
        }
        // 执行更新操作
        sql.update(updateEntity)

        // 如果员工之前已被禁用，则强制退出登录
        if (!employee.disabledFlag) {
            // 强制退出登录
//            StpUtil.logout(UserTypeEnum.ADMIN_EMPLOYEE.value + StringConst.COLON + employeeId)
        }

        // 返回成功响应
        return ResponseDTO.ok()
    }


    /**
     * 批量更新员工的删除标志
     * 此函数接收一个员工ID列表，将这些员工的删除标志设置为true，以逻辑删除的方式处理员工数据
     * 如果提供的员工ID列表为空或null，则不执行任何操作并返回成功响应
     *
     * @param employeeIdList 员工ID列表，用于指定需要更新删除标志的员工如果列表为空或null，将不执行任何操作
     * @return 返回一个ResponseDTO对象，包含执行结果的信息由于此函数不返回具体数据，因此泛型参数为String?
     */
    fun batchUpdateDeleteFlag(employeeIdList: List<String>?): ResponseDTO<String?> {
        // 检查员工ID列表是否为空或null，如果为空，则直接返回成功响应
        if (employeeIdList.isNullOrEmpty()) {
            return ResponseDTO.ok()
        }

        // 执行批量更新删除标志的操作
        sql.deleteByIds(Employee::class, employeeIdList)

        // 遍历员工ID列表，强制退出所有相关员工的登录状态
        employeeIdList.forEach { employeeId ->
            // 强制退出登录
//            StpUtil.logout(UserTypeEnum.ADMIN_EMPLOYEE.value + StringConst.COLON + employeeId)
        }

        // 返回成功响应
        return ResponseDTO.ok()
    }


    /**
     * 批量更新部门
     */
    fun batchUpdateDepartment(batchUpdateDepartmentForm: EmployeeBatchUpdateDepartmentForm): ResponseDTO<String?> {
        val employeeIdList = batchUpdateDepartmentForm.employeeIdList
        val employeeEntityList = sql.findByIds(Employee::class, employeeIdList)
        if (employeeIdList.size != employeeEntityList.size) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        }

        // 更新
        val updateList = employeeIdList.map { employeeId ->
            Employee {
                this.employeeId = employeeId
                this.departmentId = batchUpdateDepartmentForm.departmentId
            }
        }
        sql.entities.saveEntities(updateList)

        return ResponseDTO.ok()
    }

    /**
     * 更新密码
     */
    @Transactional(rollbackFor = [Throwable::class])
    fun updatePassword(requestUser: RequestUser, updatePasswordForm: EmployeeUpdatePasswordForm): ResponseDTO<String?> {
        val employeeId = updatePasswordForm.employeeId
        val employee = employeeRepository.byId(employeeId!!)
            ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        // 校验原始密码
        val oldPassword = SecurityPasswordService.getEncryptPwd(updatePasswordForm.oldPassword)
        if (oldPassword != employee.loginPwd) {
            return ResponseDTO.userErrorParam("原密码有误，请重新输入")
        }

        // 校验密码复杂度
        val validatePassComplexity = securityPasswordService.validatePasswordComplexity(updatePasswordForm.newPassword)
        if (!validatePassComplexity.ok) {
            return validatePassComplexity
        }

        // 新旧密码相同
        val newPassword = SecurityPasswordService.getEncryptPwd(updatePasswordForm.newPassword)
        if (oldPassword == newPassword) {
            return ResponseDTO.userErrorParam("新密码与原始密码相同，请重新输入")
        }

        // 根据三级等保规则，校验密码是否重复
        val passwordRepeatTimes = securityPasswordService.validatePasswordRepeatTimes(requestUser, updatePasswordForm.newPassword)
        if (!passwordRepeatTimes.ok) {
            return ResponseDTO.error(passwordRepeatTimes)
        }

        // 更新密码
        val updateEntity = Employee {
            this.employeeId = employeeId
            this.loginPwd = newPassword
        }
        sql.update(updateEntity)

        // 保存修改密码密码记录
        securityPasswordService.saveUserChangePasswordLog(requestUser, newPassword, oldPassword)

        return ResponseDTO.ok()
    }

    /**
     * 获取某个部门的员工信息
     */
    fun getAllEmployeeByDepartmentId(departmentId: String, disabledFlag: Boolean?): ResponseDTO<List<EmployeeVO>> {
        val employeeEntityList = sql.createQuery(Employee::class) {
            where(table.departmentId eq departmentId)
            where(table.disabledFlag `eq?` disabledFlag)
            select(table.fetch(EmployeeVO::class))
        }.execute()

        if (employeeEntityList.isEmpty()) {
            return ResponseDTO.ok(emptyList())
        }
        return ResponseDTO.ok(employeeEntityList)
    }

    /**
     * 重置密码
     */
    fun resetPassword(employeeId: String): ResponseDTO<String?> {
        val password = securityPasswordService.randomPassword()
        sql.createUpdate(Employee::class) {
            set(table.loginPwd, SecurityPasswordService.getEncryptPwd(password))
            where(table.employeeId eq employeeId)
        }.execute()
        return ResponseDTO.ok(password)
    }

    /**
     * 查询全部员工
     */
    fun queryAllEmployee(disabledFlag: Boolean?): ResponseDTO<List<EmployeeVO>> {
        val vos = sql.createQuery(Employee::class) {
            where(table.disabledFlag `eq?` disabledFlag)
            select(table.fetch(EmployeeVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

    /**
     * 根据登录名获取员工
     */
    fun getByLoginName(loginName: String): Employee? {
        return employeeRepository.byLoginName(loginName)
    }


}