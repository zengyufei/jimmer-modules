package com.zyf.common.utils

import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.RequestUser

/**
 * 请求用户  工具类
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-05-30 21:22:12
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
object SmartRequestUtil {
    private val REQUEST_THREAD_LOCAL = ThreadLocal<RequestUser>()

    @JvmStatic
    var requestUser: RequestUser?
        get() = REQUEST_THREAD_LOCAL.get()
        set(requestUser) {
            REQUEST_THREAD_LOCAL.set(requestUser)
        }

    @JvmStatic
    val requestUserId: String?
        get() {
            return requestUser?.userId
        }


    @JvmStatic
    fun remove() {
        REQUEST_THREAD_LOCAL.remove()
    }
}
