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
    override var userId: String? = null
    var employeeId: String? = null

    override var userType: UserTypeEnum? = null

    /** 登录账号  */
    var loginName: String? = null

    /** 员工名称  */
    override var userName: String? = null

    var actualName: String? = null
    /** 头像 */
    var avatar: String? = null

    var gender: Int? = null

    /** 手机号码 */
    var phone: String? = null

    /** 部门id */
    var departmentId: String? = null

    /** 部门名称 */
    var departmentName: String? = null

    /** 是否禁用 */
    var disabledFlag: Boolean? = null

    /** 是否为超管 */
    var administratorFlag: Boolean? = null

    /** 备注 */
    var remark: String? = null

    /** 请求ip */
    override var ip: String? = null

    /** 请求user-agent */
    override var userAgent: String? = null
}
