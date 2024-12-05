package com.zyf.login.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.zyf.service.dto.MenuVO
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime


/**
 * 登录结果信息
 *
 * @Author 1024创新实验室: 开云
 * @Date 2021-12-19 11:49:45
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class LoginResultVO : RequestEmployee() {
    /** token  */
    var token: String? = null

    /** 菜单列表  */
    var menuList: List<MenuVO>? = null

    /** 是否需要修改密码  */
    var needUpdatePwdFlag: Boolean? = null

    /** 上次登录ip  */
    var lastLoginIp: String? = null

    /** 上次登录ip地区  */
    var lastLoginIpRegion: String? = null

    /** 上次登录user-agent  */
    var lastLoginUserAgent: String? = null

    /** 上次登录时间  */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var lastLoginTime: LocalDateTime? = null
}
