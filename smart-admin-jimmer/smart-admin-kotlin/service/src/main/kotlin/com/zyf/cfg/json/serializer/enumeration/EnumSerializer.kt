package com.zyf.cfg.json.serializer.enumeration

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.zyf.common.base.BaseEnum
import com.zyf.common.constant.StringConst
import com.zyf.common.utils.SmartEnumUtil.getEnumByValue
import com.zyf.common.utils.SmartEnumUtil.getEnumDescByValue
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass

/**
 * 枚举 序列化
 *
 * @author huke
 * @date 2024年6月29日
 */
class EnumSerializer : JsonSerializer<Any>(), ContextualSerializer {
    private var enumClazz: KClass<out BaseEnum>? = null

    @Throws(IOException::class)
    override fun serialize(value: Any, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeObject(value)
        val fieldName = gen.outputContext.currentName + "Desc"
        val desc: Any?
        // 多个枚举类 逗号分割
        if (value is String && value.toString().contains(StringConst.SEPARATOR)) {
            desc = Arrays.stream(value.toString().split(StringConst.SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).mapToInt { s: String -> s.toInt() }
                .mapToObj { e: Int -> getEnumDescByValue(e, enumClazz) }.toList()
        } else {
            val anEnum = getEnumByValue(value, enumClazz)
            desc = anEnum?.desc
        }
        gen.writeObjectField(fieldName, desc)
    }

    @Throws(JsonMappingException::class)
    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        val annotation = property.getAnnotation(EnumSerialize::class.java) ?: return prov.findValueSerializer(property.type, property)
        enumClazz = annotation.value
        return this
    }
}