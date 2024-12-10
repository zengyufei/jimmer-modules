package com.zyf.runtime.convert

import com.zyf.common.base.BaseEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class StringEnumConvertorFactory : ConverterFactory<String, BaseEnum?> {
    override fun <T : BaseEnum?> getConverter(targetType: Class<T>): Converter<String, T> {
        return CONVERTER_MAP.computeIfAbsent(targetType) {
            StringEnumConvertor(targetType)
        } as Converter<String, T>
    }

    companion object {
        private val CONVERTER_MAP
                : MutableMap<Class<*>, Converter<String, *>> = ConcurrentHashMap()
    }
}
