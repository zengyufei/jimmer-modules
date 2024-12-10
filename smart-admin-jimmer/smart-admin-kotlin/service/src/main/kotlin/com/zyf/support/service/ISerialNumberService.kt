package com.zyf.support.service

import com.zyf.common.enums.SerialNumberIdEnum

/**
 * 单据序列号
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
interface ISerialNumberService {
    /**
     * 生成
     *
     * @param serialNumberIdEnum
     * @return
     */
    fun generate(serialNumberIdEnum: SerialNumberIdEnum): String?


    /**
     * 生成n个
     *
     * @param serialNumberIdEnum
     * @param count
     * @return
     */
    fun generate(serialNumberIdEnum: SerialNumberIdEnum, count: Int): List<String>
}
