package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.service.dto.DictValueVO
import com.zyf.support.DictKey
import com.zyf.support.DictValue
import jakarta.annotation.PostConstruct
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Slf4j
@Service
class DictCacheService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    companion object {
         val DICT_CACHE = ConcurrentHashMap<String, List<DictValueVO>>()
         val VALUE_CACHE = ConcurrentHashMap<String, DictValueVO>()
    }

    @PostConstruct
    fun dictCache() {
        cacheInit()
    }

    fun cacheInit() {
        val dictKeyEntityList = sql.createQuery(DictKey::class) {
            select(table)
        }.execute()
        if (dictKeyEntityList.isEmpty()) {
            return
        }

        val dictValueVOList = sql.createQuery(DictValue::class) {
            select(table.fetch(DictValueVO::class))
        }.execute()
        val valueListMap = dictValueVOList.groupBy { it.dictKeyId }

        // 字典键值对缓存
        dictKeyEntityList.forEach {
            val keyCode = it.keyCode
            val dictKeyId = it.dictKeyId
            DICT_CACHE[keyCode] = valueListMap.getOrDefault(dictKeyId, emptyList())
        }

        // 字典值缓存
        dictValueVOList.forEach {
            VALUE_CACHE[it.valueCode] = it
        }

        log.info("################# 数据字典缓存初始化完毕 ###################")
    }

    /**
     * 刷新缓存
     */
    fun cacheRefresh() {
        DICT_CACHE.clear()
        VALUE_CACHE.clear()
        cacheInit()
    }

    /**
     * 查询某个key对应的字典值列表
     */
    fun selectByKeyCode(keyCode: String): List<DictValueVO> {
        return DICT_CACHE.getOrDefault(keyCode, emptyList())
    }

    /**
     * 查询值code名称
     */
    fun selectValueNameByValueCode(valueCode: String?): String? {
        if (valueCode.isNullOrEmpty()) {
            return null
        }

        return VALUE_CACHE[valueCode]?.valueName ?: ""
    }

    fun selectValueByValueCode(valueCode: String?): DictValueVO? {
        if (valueCode.isNullOrEmpty()) {
            return null
        }
        return VALUE_CACHE[valueCode]
    }

    fun selectValueNameByValueCodeSplit(valueCodes: String?): String {
        if (valueCodes.isNullOrEmpty()) {
            return ""
        }

        return valueCodes.split(",")
            .mapNotNull { valueCode ->
                VALUE_CACHE[valueCode]?.valueName
            }
            .joinToString(",")
    }

}