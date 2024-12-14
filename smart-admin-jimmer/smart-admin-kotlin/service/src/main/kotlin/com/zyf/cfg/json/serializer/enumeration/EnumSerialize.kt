package com.zyf.cfg.json.serializer.enumeration

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.zyf.common.base.BaseEnum
import kotlin.reflect.KClass

/**
 * 枚举类 序列化 注解
 *
 * @author huke
 * @date 2024年6月29日
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = EnumSerializer::class, nullsUsing = EnumSerializer::class)
annotation class EnumSerialize(val value: KClass<out BaseEnum>)
