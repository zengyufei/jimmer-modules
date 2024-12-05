package com.zyf.common.code

/**
 * 错误码<br></br>
 * 一共分为三种： 1）系统错误、2）用户级别错误、3）未预期到的错误
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
interface ErrorCode {
    /**
     * 错误码
     */
    val code: Int

    /**
     * 错误消息
     *
     */
    val msg: String?

    /**
     * 错误等级
     */
    val level: String?



    companion object {
        /**
         * 系统等级
         */
        const val LEVEL_SYSTEM: String = "system"

        /**
         * 用户等级
         */
        const val LEVEL_USER: String = "user"

        /**
         * 未预期到的等级
         */
        const val LEVEL_UNEXPECTED: String = "unexpected"
    }
}
