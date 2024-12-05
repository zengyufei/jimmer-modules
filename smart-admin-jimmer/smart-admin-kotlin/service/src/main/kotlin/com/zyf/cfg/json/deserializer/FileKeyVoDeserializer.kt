package com.zyf.cfg.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.service.dto.FileKeyVO
import java.io.IOException

/**
 * 文件key反序列化<br></br>
 * 由于前端接收到的是序列化过的字段, 这边入库需要进行反序列化操作比较方便处理
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2022-11-24 17:15:23
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
class FileKeyVoDeserializer : JsonDeserializer<String>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): String {
        val list: MutableList<FileKeyVO> = mutableListOf()
        val objectCodec = jsonParser.codec
        val listOrObjectNode = objectCodec.readTree<JsonNode>(jsonParser)
        var deserialize: String
        try {
            if (listOrObjectNode.isArray) {
                for (node in listOrObjectNode) {
                    list.add(objectCodec.treeToValue(node, FileKeyVO::class.java))
                }
            } else {
                list.add(objectCodec.treeToValue(listOrObjectNode, FileKeyVO::class.java))
            }
            deserialize = list.joinToString(",") { it.fileKey }
        } catch (e: Exception) {
            log.error(e.message, e)
            deserialize = listOrObjectNode.asText()
        }
        return deserialize
    }
}
