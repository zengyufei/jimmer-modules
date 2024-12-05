package com.zyf.common.code

import com.zyf.common.code.ErrorCodeRangeContainer.register


/**
 * 注册code状态码 <br></br>
 * ps：为什么要在此处不那么优雅的手动注册？
 * 主要是为了能统一、清晰、浏览当前定义的所有状态码
 * 方便后续维护
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021/09/27 23:09
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
object ErrorCodeRegister {
    init {
        // 系统 错误码

        register(SystemErrorCode::class.java, 10001, 20000)

        // 意外 错误码
        register(UnexpectedErrorCode::class.java, 20001, 30000)

        // 用户 通用错误码
        register(UserErrorCode::class.java, 30001, 40000)
    }


    fun initialize(): Int {
        return ErrorCodeRangeContainer.initialize()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        initialize()
    }
}
