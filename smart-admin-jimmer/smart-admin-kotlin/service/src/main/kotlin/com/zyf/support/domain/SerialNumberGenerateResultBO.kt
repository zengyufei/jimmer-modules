package com.zyf.support.domain

import java.time.LocalDateTime

/**
 * 单据序列号 生成结果
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class SerialNumberGenerateResultBO {
    /**
     * 序号id
     */
    var serialNumberId: String? = null

    /**
     * 是否重置的初始值
     */
    var isReset: Boolean? = null

    /**
     * 上次生成的数字
     */
    var lastNumber: Long? = null

    /**
     * 上次生成的时间
     */
    var lastTime: LocalDateTime? = null

    /**
     * 生成的 number  集合
     */
    var numberList: List<Long>? = null
}
