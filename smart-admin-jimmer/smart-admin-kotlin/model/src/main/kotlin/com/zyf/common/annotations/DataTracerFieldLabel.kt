package com.zyf.common.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

/**
 * 字段标签
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class DataTracerFieldLabel(
    /**
     * 本属性的注释信息
     * @return
     */
    val value: String
)
