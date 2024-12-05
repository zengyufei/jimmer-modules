package com.zyf.cfg.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException

/**
 * Long类型序列化
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2020-06-02 22:55:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class LongJsonDeserializer : JsonDeserializer<Long?>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Long? {
        val value = jsonParser.text
        return try {
            value?.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }
}