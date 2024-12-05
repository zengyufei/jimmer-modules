package com.zyf.cfg.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import java.io.IOException

/**
 * 字典反序列化
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-08-12 22:17:53
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
class DictValueVoDeserializer : JsonDeserializer<String>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): String {
        val list: MutableList<String> = ArrayList()
        val objectCodec = jsonParser.codec
        val listOrObjectNode = objectCodec.readTree<JsonNode>(jsonParser)
        var deserialize = ""
        try {
            if (listOrObjectNode.isArray) {
                for (node in listOrObjectNode) {
                    list.add(node.asText())
                }
            } else {
                list.add(listOrObjectNode.asText())
            }
            deserialize = java.lang.String.join(",", list)
        } catch (e: Exception) {
            log.error(e.message, e)
            deserialize = listOrObjectNode.asText()
        }
        return deserialize
    }
}
