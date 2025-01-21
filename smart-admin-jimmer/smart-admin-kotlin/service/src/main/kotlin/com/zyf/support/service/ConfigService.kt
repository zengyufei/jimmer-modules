package com.zyf.support.service

import cn.hutool.json.XMLTokener.entity
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.annotations.SmartReload
import com.zyf.common.code.ErrorCode
import com.zyf.common.code.UserErrorCode
import com.zyf.common.constant.ReloadConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.ConfigKeyEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.service.dto.ConfigAddForm
import com.zyf.service.dto.ConfigQueryForm
import com.zyf.service.dto.ConfigUpdateForm
import com.zyf.service.dto.ConfigVO
import com.zyf.support.Config
import com.zyf.support.configId
import com.zyf.support.configKey
import jakarta.annotation.PostConstruct
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.count
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.`ilike?`
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Slf4j
@Service
class ConfigService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    companion object {

        /**
         * 一个简单的系统配置缓存
         */
        private val CONFIG_CACHE = ConcurrentHashMap<String, ConfigVO>()
    }


    /**
     * 初始化系统设置缓存
     */
    @PostConstruct
    private fun loadConfigCache() {
        CONFIG_CACHE.clear()
        val entityList = sql.createQuery(Config::class) {
            select(table.fetch(ConfigVO::class))
        }.execute()
        if (entityList.isEmpty()) {
            return
        }
        entityList.forEach {
            CONFIG_CACHE[it.configKey.lowercase()] = it
        }
        log.info("################# 系统配置缓存初始化完毕:{} ###################", CONFIG_CACHE.size)
    }


    /**
     * 分页查询系统配置
     */
    fun queryConfigPage(pageBean: PageBean, queryForm: ConfigQueryForm): PageResult<ConfigVO> {
        return sql.createQuery(Config::class) {
            orderBy(pageBean)
            where(queryForm)
            select(table.fetch(ConfigVO::class))
        }.page(pageBean)
    }

    /**
     * 添加系统配置
     */
    fun add(configAddForm: ConfigAddForm): ErrorCode? {
        val fetchOne = sql.createQuery(Config::class) {
            where(table.configKey eq configAddForm.configKey)
            select(count(table))
        }.fetchOne()
        if (fetchOne > 0) {
            return UserErrorCode.ALREADY_EXIST
        }

        val modifiedEntity = sql.insert(configAddForm).modifiedEntity

        // 刷新缓存
        refreshConfigCache(modifiedEntity.configId)
        return null
    }

    /**
     * 更新系统配置
     */
    fun updateConfig(updateDTO: ConfigUpdateForm): ErrorCode? {
        val configId = updateDTO.configId

        sql.findById(Config::class, configId) ?: return UserErrorCode.DATA_NOT_EXIST

        val fetchOne = sql.createQuery(Config::class) {
            where(table.configKey eq updateDTO.configKey)
            where(table.configId ne configId)
            select(count(table))
        }.fetchOne()

        if (fetchOne > 0) {
            return UserErrorCode.ALREADY_EXIST
        }

        // 更新数据
        sql.update(updateDTO)

        // 刷新缓存
        refreshConfigCache(configId)
        return null
    }

    /**
     * 刷新系统设置缓存
     */
    private fun refreshConfigCache(configId: String) {
        // 重新查询 加入缓存
        val configEntity = sql.findById(ConfigVO::class, configId) ?: return
        CONFIG_CACHE[configEntity.configKey.lowercase()] = configEntity
    }


    /**
     * 查询配置缓存
     */
    fun getConfig(configKey: ConfigKeyEnum): ConfigVO? {
        return getConfig(configKey.value)
    }

    /**
     * 查询配置缓存
     */
    fun getConfig(configKey: String): ConfigVO? {
        if (configKey.isBlank()) {
            return null
        }
        return CONFIG_CACHE[configKey.lowercase()]
    }

    /**
     * 查询配置缓存参数
     */
    fun getConfigValue(configKey: ConfigKeyEnum): String? {
        return getConfig(configKey)?.configValue
    }

    @SmartReload(ReloadConst.CONFIG_RELOAD)
    fun configReload(param: String) {
        loadConfigCache()
    }



    /**
     * 根据参数key查询 并转换为对象
     */
    fun <T> getConfigValue2Obj(configKey: ConfigKeyEnum, clazz: Class<T>): T? {
        val configValue = getConfigValue(configKey)
        return objectMapper.readValue(configValue, clazz)
    }

    /**
     * 更新系统配置
     */
    fun updateValueByKey(key: ConfigKeyEnum, value: String): ResponseDTO<String?> {
        val config = getConfig(key) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        // 更新数据
        val configId = config.configId

        val entity = Config{
            this.configId = configId
            this.configValue = value
        }
        sql.update(entity)

        // 刷新缓存
        refreshConfigCache(configId)
        return ResponseDTO.ok()
    }

}