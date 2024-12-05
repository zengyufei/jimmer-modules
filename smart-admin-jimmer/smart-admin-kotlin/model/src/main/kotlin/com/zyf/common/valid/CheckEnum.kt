package com.zyf.common.valid

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


/**
 * 自定义的属性校验注解，为了方便与校验属性的值是否为合法的枚举值
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2017/11/11 15:31
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumValidator::class]) // 自定义验证的处理类
annotation class CheckEnum(
    /**
     * 默认的错误提示信息
     *
     * @return String
     */
    val message: String,
    /**
     * 枚举类对象 必须实现BaseEnum接口
     *
     */
    val value: KClass<out Enum<*>>,
    /**
     * 是否必须
     *
     * @return boolean
     */
    val required: Boolean = false,  //下面这两个属性必须添加 :不然会报错
    val groups: Array<KClass<*>> = [], val payload: Array<KClass<out Payload>> = []
)
