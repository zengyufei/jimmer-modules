package com.zyf.cfg.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException

/**
 * Long类型序列化
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2020-06-02 22:55:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
class LongJsonSerializer : JsonSerializer<Long?>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: Long?, gen: JsonGenerator, serializerProvider: SerializerProvider) {
        if (null == value) {
            gen.writeNull()
            return
        }
        // js中最大安全整数16位 Number.MAX_SAFE_INTEGER
        val longStr = value.toString()
        if (longStr.length > 16) {
            gen.writeString(longStr)
        } else {
            gen.writeNumber(value)
        }
    }

    companion object {
        val INSTANCE: LongJsonSerializer = LongJsonSerializer()
    }
}