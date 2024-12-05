package com.zyf.cfg.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.math.BigDecimal

/**
 * 数字序列化
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/8/20 21:04
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class BigDecimalNullZeroSerializer : JsonSerializer<BigDecimal?>() {
    @Throws(IOException::class)
    override fun serialize(value: BigDecimal?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        if (value == null) {
            jsonGenerator.writeNumber(BigDecimal.ZERO)
            return
        }
        jsonGenerator.writeNumber(value)
    }
}