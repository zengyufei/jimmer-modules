package com.zyf.cfg.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.zyf.cfg.json.datamasking.DataMasking
import com.zyf.cfg.json.datamasking.DataMaskingTypeEnum
import com.zyf.cfg.json.datamasking.SmartDataMaskingUtil.Companion.dataMasking
import org.apache.commons.lang3.ObjectUtils
import java.io.IOException

/**
 * 脱敏序列化
 *
 * @author 罗伊
 * @description:
 * @date 2024/7/21 4:39 下午
 */
class DataMaskingSerializer : JsonSerializer<Any>(), ContextualSerializer {
    private var typeEnum: DataMaskingTypeEnum? = null

    @Throws(IOException::class)
    override fun serialize(value: Any, jsonGenerator: JsonGenerator, serializers: SerializerProvider) {
        if (ObjectUtils.isEmpty(value)) {
            jsonGenerator.writeObject(value)
            return
        }

        if (typeEnum == null) {
            jsonGenerator.writeObject(dataMasking(value.toString()))
            return
        }

        jsonGenerator.writeObject(dataMasking(value, typeEnum))
    }

    @Throws(JsonMappingException::class)
    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        // 判断beanProperty是不是空

        val annotation = property.getAnnotation(DataMasking::class.java) ?: return prov.findValueSerializer(property.type, property)

        typeEnum = annotation.value
        return this
    }
}