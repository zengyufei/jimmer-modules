package com.zyf.common.valid

import com.zyf.common.base.BaseEnum
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


/**
 * 枚举类校验器
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2017/11/11 15:34
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class EnumValidator : ConstraintValidator<CheckEnum, Any?> {
    /**
     * 枚举类实例集合
     */
    private var enumValList: List<Any>? = null

    /**
     * 是否必须
     */
    private var required = false

    override fun initialize(constraintAnnotation: CheckEnum) {
        // 获取注解传入的枚举类对象
        required = constraintAnnotation.required
        val enumClass: Class<out Enum<*>> = constraintAnnotation.value.java
        require(BaseEnum::class.java.isAssignableFrom(enumClass)) {
            "枚举类必须实现 BaseEnum 接口"
        }
        enumValList = enumClass.enumConstants.map { (it as BaseEnum).value }
    }

    override fun isValid(value: Any?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        // 判断是否必须
        if (null == value) {
            return !required
        }

        if (value is List<*>) {
            // 如果为 List 集合数据
            return this.checkList(value as List<Any>)
        }

        // 校验是否为合法的枚举值
        return enumValList!!.contains(value)
    }

    /**
     * 校验集合类型
     *
     */
    private fun checkList(list: List<Any>): Boolean {
        if (required && list.isEmpty()) {
            // 必须的情况下 list 不能为空
            return false
        }
        // 校验是否重复
        val count = list.stream().distinct().count()
        if (count != list.size.toLong()) {
            return false
        }
        return enumValList!!.containsAll(list)
    }
}
