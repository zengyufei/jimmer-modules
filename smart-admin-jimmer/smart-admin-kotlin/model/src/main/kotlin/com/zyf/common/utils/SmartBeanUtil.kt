package com.zyf.common.utils

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.beans.BeanUtils

/**
 * bean相关工具类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2018-01-15 10:48:23
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
object SmartBeanUtil {

    /**
     * 验证器
     */
    private val VALIDATOR: Validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * 复制bean的属性
     *
     * @param source 源 要复制的对象
     * @param target 目标 复制到此对象
     */
    fun copyProperties(source: Any, target: Any) {
        BeanUtils.copyProperties(source, target)
    }

    /**
     * 复制对象
     *
     * @param source 源 要复制的对象
     * @param target 目标 复制到此对象
     * @return
     */
    fun <T> copy(source: Any?, target: Class<T>): T? {
        if (source == null || target == null) {
            return null
        }
        return try {
            val newInstance = target.getDeclaredConstructor().newInstance()
            BeanUtils.copyProperties(source, newInstance!!)
            newInstance
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * 复制list
     *
     * @param source
     * @param target
     * @return
     */
    fun <T, K> copyList(source: List<T>?, target: Class<K>): MutableList<K> {
        if (source.isNullOrEmpty()) {
            return mutableListOf()
        }
        return source.map { copy(it, target)!! }.toMutableList()
    }

    /**
     * 手动验证对象 Model的属性
     * 需要配合 hibernate-validator 校验注解
     *
     * @param t
     * @return String 返回null代表验证通过，否则返回错误的信息
     */
    fun <T> verify(t: T): String? {
        // 获取验证结果
        val validate: Set<ConstraintViolation<T>> = VALIDATOR.validate(t)
        if (validate.isEmpty()) {
            // 验证通过
            return null
        }
        // 返回错误信息
        val messageList = validate.map { it.message }
        return messageList.toString()
    }
}