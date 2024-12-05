package com.zyf.cfg.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.zyf.support.service.FileService
import org.apache.commons.lang3.StringUtils
import java.io.IOException

/**
 * 文件key进行序列化对象
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/8/15 22:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class FileKeySerializer(private val fileService: FileService?) : JsonSerializer<String?>() {
    @Throws(IOException::class)
    override fun serialize(value: String?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        if (StringUtils.isEmpty(value)) {
            jsonGenerator.writeString(value)
            return
        }
        if (fileService == null) {
            jsonGenerator.writeString(value)
            return
        }
        val responseDTO = fileService.getFileUrl(value)
        if (responseDTO.ok) {
            jsonGenerator.writeString(responseDTO.data)
            return
        }
        jsonGenerator.writeString(value)
    }
}
