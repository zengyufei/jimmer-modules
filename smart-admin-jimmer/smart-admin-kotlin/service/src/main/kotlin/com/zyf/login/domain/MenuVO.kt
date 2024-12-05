package com.zyf.login.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime


/**
 * 菜单
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class MenuVO : MenuBaseForm() {
    /** 菜单ID  */
    val menuId: Long? = null

    /** 创建时间  */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    val createTime: LocalDateTime? = null

    /** 创建人  */
    val createUserId: Long? = null

    /** 更新时间  */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    val updateTime: LocalDateTime? = null

    /** 更新人  */
    val updateUserId: Long? = null
}
