package com.zyf.common.domain

import com.zyf.common.enums.UserTypeEnum


/**
 * 请求用户
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-12-21 19:55:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
interface RequestUser {
    /**
     * 请求用户id
     *
     * @return
     */
    val userId: String?

    /**
     * 请求用户名称
     *
     * @return
     */
    val userName: String?

    /**
     * 获取用户类型
     */
    val userType: UserTypeEnum?

    /**
     * 获取请求的IP
     *
     * @return
     */
    val ip: String?

    /**
     * 获取请求 user-agent
     *
     * @return
     */
    val userAgent: String?
}
