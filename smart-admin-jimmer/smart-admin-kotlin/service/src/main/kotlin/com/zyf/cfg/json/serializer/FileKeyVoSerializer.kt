package com.zyf.cfg.json.serializer

import cn.hutool.extra.spring.SpringUtil
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.zyf.support.service.FileService
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.util.*

/**
 * 文件key进行序列化对象
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/8/15 22:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class FileKeyVoSerializer() : JsonSerializer<String>() {


    @Throws(IOException::class)
    override fun serialize(value: String, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        if (StringUtils.isEmpty(value)) {
            jsonGenerator.writeObject(mutableListOf<String>())
            return
        }
        val fileKeyList = value.split(",").toMutableList()
        val fileKeyVOList = SpringUtil.getBean(FileService::class.java).getFileList(fileKeyList)
        jsonGenerator.writeObject(fileKeyVOList)
    }
}
