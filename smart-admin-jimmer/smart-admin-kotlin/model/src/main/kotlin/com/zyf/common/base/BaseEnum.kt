package com.zyf.common.base


/**
 * 枚举类接口
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2018-07-17 21:22:12
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
interface BaseEnum {
    /**
     * 获取枚举类的值
     *
     * @return
     */
    val value: Any

    /**
     * 获取枚举类的说明
     *
     * @return String
     */
    val desc: String

    /**
     * 比较参数是否与枚举类的value相同
     *
     * @param value
     * @return boolean
     */
    fun equalsValue(value: Any): Boolean {
        return this.value == value
    }

    /**
     * 比较枚举类是否相同
     *
     * @param baseEnum
     * @return boolean
     */
    fun equals(baseEnum: BaseEnum): Boolean {
        return value == baseEnum.value && desc == baseEnum.desc
    }
}
