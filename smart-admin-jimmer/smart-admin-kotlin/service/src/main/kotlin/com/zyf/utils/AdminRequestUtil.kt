package com.zyf.utils

import com.zyf.common.utils.SmartRequestUtil
import com.zyf.login.domain.RequestEmployee


/**
 * admin 端的请求工具类
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/7/28 19:39:21
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)，Since 2012
 */
object AdminRequestUtil {
    @JvmStatic
    val requestUser: RequestEmployee
        get() = SmartRequestUtil.requestUser!! as RequestEmployee

    @JvmStatic
    val requestUserId: String
        get() {
            return requestUser.userId
        }
}
