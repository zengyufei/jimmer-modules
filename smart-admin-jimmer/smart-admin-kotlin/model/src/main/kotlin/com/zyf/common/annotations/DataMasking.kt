package com.zyf.common.annotations

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.zyf.common.enums.DataMaskingTypeEnum
import com.zyf.common.json.DataMaskingSerializer

/**
 * 脱敏注解
 *
 * @author 罗伊
 * @description:
 * @date 2024/7/21 4:39 下午
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DataMaskingSerializer::class, nullsUsing = DataMaskingSerializer::class)
annotation class DataMasking(val value: DataMaskingTypeEnum = DataMaskingTypeEnum.COMMON)
