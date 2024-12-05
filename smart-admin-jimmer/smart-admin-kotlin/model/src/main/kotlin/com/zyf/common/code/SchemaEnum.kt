package com.zyf.common.code

import com.zyf.common.base.BaseEnum
import kotlin.reflect.KClass


/**
 * 枚举类字段属性的 自定义 swagger 注解
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2019/05/16 23:18
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SchemaEnum(
    /**
     * 枚举类对象
     *
     */
    val value: KClass<out BaseEnum>,
    val example: String = "",
    val hidden: Boolean = false,
    val required: Boolean = true,
    val dataType: String = "",
    val desc: String = ""
)
