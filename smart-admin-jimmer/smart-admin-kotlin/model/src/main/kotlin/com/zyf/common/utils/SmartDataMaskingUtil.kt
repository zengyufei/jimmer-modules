package com.zyf.common.utils

import cn.hutool.core.util.DesensitizedUtil
import cn.hutool.core.util.StrUtil
import com.zyf.common.annotations.DataMasking
import com.zyf.common.enums.DataMaskingTypeEnum
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import java.beans.IntrospectionException
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap

/**
 * 脱敏工具类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2024-07-23 21:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
class SmartDataMaskingUtil {


    companion object {

        /**
         * 类 加注解字段缓存
         */
        private val fieldMap = ConcurrentHashMap<Class<*>, MutableList<Field>>()

        @JvmStatic
        fun dataMasking(value: String): String {
            if (StringUtils.isBlank(value)) {
                return value
            }

            if (value.length < 4) {
                return StrUtil.hide(value, 0, value.length)
            }

            val valueLength = value.length
            val startHideIndex = getHideStartIndex(valueLength)
            val endHideIndex = getHideEndIndex(valueLength)
            return StrUtil.hide(value, startHideIndex, endHideIndex)
        }

        @JvmStatic
        fun dataMasking(value: Any?, dataType: DataMaskingTypeEnum?): Any? {
            if (value == null) {
                return null
            }

            if (dataType == null) {
                return dataMasking(value.toString())
            }

            return when (dataType) {
                DataMaskingTypeEnum.PHONE -> DesensitizedUtil.mobilePhone(value.toString())
                DataMaskingTypeEnum.CHINESE_NAME -> DesensitizedUtil.chineseName(value.toString())
                DataMaskingTypeEnum.ID_CARD -> DesensitizedUtil.idCardNum(value.toString(), 6, 2)
                DataMaskingTypeEnum.FIXED_PHONE -> DesensitizedUtil.fixedPhone(value.toString())
                DataMaskingTypeEnum.ADDRESS -> StrUtil.hide(value.toString(), 6, value.toString().length - 1)
                DataMaskingTypeEnum.EMAIL -> DesensitizedUtil.email(value.toString())
                DataMaskingTypeEnum.PASSWORD -> DesensitizedUtil.password(value.toString())
                DataMaskingTypeEnum.CAR_LICENSE -> DesensitizedUtil.carLicense(value.toString())
                DataMaskingTypeEnum.BANK_CARD -> DesensitizedUtil.bankCard(value.toString())
                DataMaskingTypeEnum.USER_ID -> DesensitizedUtil.userId()
                else -> dataMasking(value.toString())
            }
        }


        /**
         * 批量脱敏
         */
        @JvmStatic
        @Throws(IntrospectionException::class, InvocationTargetException::class, IllegalAccessException::class)
        fun <T : Any> dataMasking(objectList: Collection<T?>) {
            if (CollectionUtils.isEmpty(objectList)) {
                return
            }

            for (obj in objectList) {
                dataMasking(obj!!)
            }
        }


        /**
         * 单个脱敏
         */
        @JvmStatic
        @Throws(IntrospectionException::class, InvocationTargetException::class, IllegalAccessException::class)
        fun <T : Any> dataMasking(value: T) {
            val tClass: Class<*> = value::class.java
            val fieldList = getField(value)
            for (field in fieldList) {
                field.isAccessible = true
                var fieldValue = ""
                try {
                    val pd = PropertyDescriptor(field.name, tClass)
                    val getMethod = pd.readMethod
                    val vvalue = getMethod.invoke(value)
                    if (vvalue != null) {
                        fieldValue = vvalue.toString()
                    }
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
                if (StringUtils.isBlank(fieldValue)) {
                    continue
                }
                val valueLength = fieldValue.length
                val startHideIndex = getHideStartIndex(valueLength)
                val endHideIndex = getHideEndIndex(valueLength)
                try {
                    field[value] = StrUtil.hide(fieldValue, startHideIndex, endHideIndex)
                } catch (e1: Exception) {
                    throw RuntimeException(e1)
                }
            }
        }

        private fun getHideStartIndex(totalLength: Int): Int {
            return if (totalLength <= 4) {
                1
            } else if (totalLength <= 6) {
                1
            } else if (totalLength <= 10) {
                2
            } else if (totalLength <= 18) {
                3
            } else if (totalLength <= 27) {
                5
            } else if (totalLength <= 34) {
                7
            } else if (totalLength <= 41) {
                9
            } else {
                15
            }
        }

        private fun getHideEndIndex(totalLength: Int): Int {
            return if (totalLength <= 4) {
                totalLength - 1
            } else if (totalLength <= 6) {
                totalLength - 2
            } else if (totalLength <= 10) {
                totalLength - 2
            } else if (totalLength <= 18) {
                totalLength - 4
            } else if (totalLength <= 27) {
                totalLength - 6
            } else if (totalLength <= 34) {
                totalLength - 8
            } else if (totalLength <= 41) {
                totalLength - 10
            } else {
                totalLength - 16
            }
        }


        @Throws(IntrospectionException::class)
        @JvmStatic
        fun getField(`object`: Any): List<Field> {
            // 从缓存中查询
            val tClass: Class<*> = `object`.javaClass
            var fieldList = fieldMap[tClass]
            if (null != fieldList) {
                return fieldList
            }

            // 这一段递归代码 是为了 从父类中获取属性
            var tempClass: Class<*>? = tClass
            fieldList = ArrayList()
            while (tempClass != null) {
                val declaredFields = tempClass.declaredFields
                for (field in declaredFields) {
                    var stringField: Boolean
                    try {
                        val pd = PropertyDescriptor(field.name, tClass)
                        val getMethod = pd.readMethod
                        val returnType = getMethod.genericReturnType
                        stringField = "java.lang.String" == returnType.typeName
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                    if (field.isAnnotationPresent(DataMasking::class.java) && stringField) {
                        field.isAccessible = true
                        fieldList.add(field)
                    }
                }
                tempClass = tempClass.superclass
            }
            fieldMap[tClass] = fieldList
            return fieldList
        }

    }
    // @JvmStatic
    // fun main(args: Array<String>) {
    //     println(dataMasking("a", null))
    //     println(dataMasking("ab", null))
    //     println(dataMasking("abc", null))
    //     println(dataMasking("abcd", null))
    //     println(dataMasking("abcde", null))
    // }

}