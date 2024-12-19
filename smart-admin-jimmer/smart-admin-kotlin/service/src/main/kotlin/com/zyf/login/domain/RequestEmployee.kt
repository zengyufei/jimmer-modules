package com.zyf.login.domain

import com.zyf.common.domain.RequestUser
import com.zyf.common.enums.UserTypeEnum


/**
 * 请求员工登录信息
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2021/8/4 21:15
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
open class RequestEmployee : RequestUser {
    /** 员工id  */
    override lateinit var userId: String

    lateinit var employeeId: String

    override lateinit var userType: UserTypeEnum

    /** 登录账号  */
    lateinit var loginName: String

    /** 员工名称  */
    override lateinit var userName: String

    var actualName: String? = null

    /** 头像 */
    var avatar: String? = null

    var gender: Int? = null

    /** 手机号码 */
    var phone: String? = null

    /** 部门id */
    lateinit var departmentId: String

    /** 部门名称 */
    lateinit var departmentName: String

    /** 是否禁用 */
    var disabledFlag: Boolean = false

    /** 是否为超管 */
    var administratorFlag: Boolean = false

    /** 备注 */
    var remark: String? = null

    /** 请求ip */
    override var ip: String? = null

    /** 请求user-agent */
    override var userAgent: String? = null
}
