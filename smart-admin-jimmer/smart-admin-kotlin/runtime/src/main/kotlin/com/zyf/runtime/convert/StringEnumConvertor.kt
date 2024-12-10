package com.zyf.runtime.convert

import com.zyf.common.base.BaseEnum
import org.springframework.core.convert.converter.Converter

class StringEnumConvertor<T : BaseEnum?>(targetType: Class<T>) : Converter<String, T?> {
    private val enumMap: MutableMap<String, T> = HashMap()

    init {
        // 将枚举按照 convertBy 返回的标志转为 map，提高匹配效率
        for (enumConstant in targetType.enumConstants) {
            enumMap[enumConstant!!.value.toString()] = enumConstant
        }
    }

    override fun convert(source: String): T? {
        return enumMap[source]
    }
}
