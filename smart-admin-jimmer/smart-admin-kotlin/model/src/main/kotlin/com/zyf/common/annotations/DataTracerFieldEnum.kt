package com.zyf.common.annotations

import com.zyf.common.base.BaseEnum
import kotlin.reflect.KClass

/**
 * 字段枚举
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class DataTracerFieldEnum(val enumClass: KClass<out BaseEnum> = BaseEnum::class)