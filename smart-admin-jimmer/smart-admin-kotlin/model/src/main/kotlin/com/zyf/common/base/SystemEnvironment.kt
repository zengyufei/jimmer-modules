package com.zyf.common.base

import com.zyf.common.enums.SystemEnvironmentEnum


/**
 * 系统环境
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021/8/13 21:06:11
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class SystemEnvironment(
    /**
     * 是否位生产环境
     */
    val isProd: Boolean = false,
    /**
     * 项目名称
     */
    val projectName: String?,
    /**
     * 当前环境
     */
    val currentEnvironment: SystemEnvironmentEnum
) {
}
