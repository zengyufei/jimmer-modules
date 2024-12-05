package com.zyf.common.utils

import com.zyf.common.base.BaseEnum
import kotlin.reflect.KClass

/**
 * 枚举工具类
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2017/10/10 18:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
object SmartEnumUtil {
    /**
     * 校验参数与枚举类比较是否合法
     *
     * @param value     参数
     * @param enumClass 枚举类必须实现BaseEnum接口
     * @return boolean
     * @Author 胡克
     */
    @JvmStatic
    inline fun <reified T> checkEnum(value: Any?): Boolean where T : Enum<T>, T : BaseEnum {
        if (null == value) {
            return false
        }
        val enumValues = enumValues<T>()
        return enumValues.any {
            val enumValue = it.value
            if (enumValue is String && value is String) {
                enumValue.equals(value, ignoreCase = true)
            } else {
                it.value == value
            }
        }
    }

    /**
     * 创建一个具有唯一array值的数组，每个值不包含在其他给定的数组中。
     *
     * @param enumClass
     * @param exclude
     * @param <T>
     * @return
    </T> */
//    fun <T : BaseEnum?> differenceValueList(enumClass: Class<out BaseEnum?>, vararg exclude: T): List<Any> {
//        val valueSet = HashSet<Any>()
//        if (exclude != null) {
//            valueSet.addAll(Stream.of<T>(*exclude).map<Any>(BaseEnum::value).collect(Collectors.toSet<Any>()))
//        }
//
//        return Stream.of(enumClass.getEnumConstants())
//            .filter { e: Array<BaseEnum?> -> !valueSet.contains(e.value) }
//            .map<Any>(BaseEnum::getValue)
//            .collect(Collectors.toList<Any>())
//    }

    /**
     * 获取枚举类的说明 value : info 的形式
     *
     * @param enumClass
     * @return String
     */
//    fun getEnumDesc(enumClass: Class<out BaseEnum?>): String {
//        val enums: Array<BaseEnum?> = enumClass.getEnumConstants()
//        // value : info 的形式
//        val sb = StringBuilder()
//        for (baseEnum in enums) {
//            sb.append(baseEnum?.value).append("：").append(baseEnum?.desc).append("，")
//        }
//        return sb.toString()
//    }

    /**
     * 获取与参数相匹配的枚举类实例的 说明
     *
     * @param value     参数
     * @param enumClass 枚举类必须实现BaseEnum接口
     * @return String 如无匹配枚举则返回null
     */
    @JvmStatic
    inline fun <reified T> getEnumDescByValue(value: Any?): String? where T : Enum<T>, T : BaseEnum {
        if (null == value) {
            return null
        }
        return enumValues<T>().find {
            val enumValue = it.value
            if (enumValue is String && value is String) {
                enumValue.equals(value, ignoreCase = true)
            } else {
                it.value == value
            }
        }?.desc
    }

    @JvmStatic
    fun <T : BaseEnum> getEnumDescByValue(value: Any?, enumClass: KClass<T>?): String? {
        if (null == value) {
            return null
        }
        return enumClass?.java?.enumConstants?.find {
            val enumValue = it.value
            if (enumValue is String && value is String) {
                enumValue.equals(value, ignoreCase = true)
            } else {
                it.value == value
            }
        }?.desc
    }

    @JvmStatic
    fun <T : BaseEnum> getEnumDescByValueList(values: Collection<*>, enumClass: KClass<T>?): String? {
        if (values.isEmpty()) {
            return ""
        }

        return enumClass?.java?.enumConstants
            ?.filter { enum -> values.contains(enum.value) }
            ?.joinToString(",") { it.desc }
    }

    /**
     * 根据参数获取枚举类的实例
     *
     * @param value     参数
     * @param enumClass 枚举类必须实现BaseEnum接口
     * @return BaseEnum 无匹配值返回null
     * @Author 胡克
     */
    @JvmStatic
    inline fun <reified T> getEnumByValue(value: Any?): T? where T : Enum<T>, T : BaseEnum {
        if (null == value) {
            return null
        }
        return enumValues<T>().find {
            val enumValue = it.value
            if (enumValue is String && value is String) {
                enumValue.equals(value, ignoreCase = true)
            } else {
                it.value == value
            }
        }
    }

    @JvmStatic
    fun <T : BaseEnum> getEnumByValue(value: Any?, enumClass: KClass<T>?): T? {
        if (null == value) {
            return null
        }
        return enumClass?.java?.enumConstants?.find {
            val enumValue = it.value
            if (enumValue is String && value is String) {
                enumValue.equals(value, ignoreCase = true)
            } else {
                it.value == value
            }
        }
    }

    /**
     * 根据实例描述与获取枚举类的实例
     *
     * @param desc      参数描述
     * @param enumClass 枚举类必须实现BaseEnum接口
     * @return BaseEnum 无匹配值返回null
     * @Author 胡克
     */
    @JvmStatic
    inline fun <reified T> getEnumByDesc(desc: String): T? where T : Enum<T>, T : BaseEnum {
        return enumValues<T>().find { it.desc.equals(desc, ignoreCase = true) }
    }

    @JvmStatic
    inline fun <reified T> getEnumByName(name: String?): T? where T : Enum<T>, T : BaseEnum {
        return enumValues<T>().find { it.name.equals(name, ignoreCase = true) }
    }


//    /**
//     * 根据lambda getter/setter 注入
//     *
//     * @param list
//     * @param getter
//     * @param setter
//     * @param enumClass
//     * @param <T>
//    </T> */
//    inline fun <reified T>  inject(
//        list: List<T>?,
//        getter: Function<T, String?>,
//        setter: BiConsumer<T, String?>
//    ) where T : Enum<T>, T : BaseEnum {
//        if (list == null || list.isEmpty()) {
//            return
//        }
//        for (t in list) {
//            val enumValue = getter.apply(t)
//            if (enumValue != null) {
//                val enumDescByValue = getEnumDescByValue(enumValue)
//                setter.accept(t, enumDescByValue)
//            }
//        }
//    }
}
