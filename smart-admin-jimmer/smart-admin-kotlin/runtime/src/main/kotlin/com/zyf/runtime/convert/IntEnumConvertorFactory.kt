package com.zyf.runtime.convert

import com.zyf.common.base.BaseEnum
import com.zyf.runtime.convert.StringEnumConvertorFactory.Companion
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class IntEnumConvertorFactory : ConverterFactory<Int, BaseEnum?> {
    override fun <T : BaseEnum?> getConverter(targetType: Class<T>): Converter<Int, T> {
        return CONVERTER_MAP.computeIfAbsent(targetType) {
            IntEnumConvertor(targetType)
        } as Converter<Int, T>
    }

    companion object {
        private val CONVERTER_MAP
                : MutableMap<Class<*>, Converter<Int, *>> = ConcurrentHashMap()
    }
}
