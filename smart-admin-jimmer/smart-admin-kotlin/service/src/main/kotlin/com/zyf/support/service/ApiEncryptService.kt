package com.zyf.support.service

/**
 * 接口加密、解密 Service
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/21 11:41:46
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
interface ApiEncryptService {
    /**
     * 解密
     * @param data
     * @return
     */
    fun decrypt(data: String?): String

    /**
     * 加密
     *
     * @param data
     * @return
     */
    fun encrypt(data: String?): String
}
