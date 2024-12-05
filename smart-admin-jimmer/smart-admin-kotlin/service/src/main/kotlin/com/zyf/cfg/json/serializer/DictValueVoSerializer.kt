package com.zyf.cfg.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.zyf.service.dto.DictValueVO
import com.zyf.support.service.DictCacheService
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.util.*
import java.util.function.Consumer

/**
 * 字典序列化
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-08-12 22:17:53
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class DictValueVoSerializer(private val dictCacheService: DictCacheService) : JsonSerializer<String>() {
    @Throws(IOException::class)
    override fun serialize(value: String, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        if (StringUtils.isEmpty(value)) {
            jsonGenerator.writeObject(mutableListOf<String>())
            return
        }

        val valueCodeList = value.split(",").toMutableList()
        val dictValueVOList: MutableList<DictValueVO> = ArrayList()
        valueCodeList.forEach(Consumer { e: String? ->
            if (StringUtils.isNotBlank(e)) {
                val dictValueVO = dictCacheService.selectValueByValueCode(e)
                if (dictValueVO != null) {
                    dictValueVOList.add(dictValueVO)
                }
            }
        })
        jsonGenerator.writeObject(dictValueVOList)
    }
}
