package com.zyf.common.code

import com.zyf.common.base.BaseEnum


/**
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/25 09:47:13
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)，Since 2012
 */
enum class DataTypeEnum(override val value: Int, override val desc: String) : BaseEnum {
    /**
     * 普通数据
     */
    NORMAL(1, "普通数据"),

    /**
     * 加密数据
     */
    ENCRYPT(10, "加密数据"),
}
