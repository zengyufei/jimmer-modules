package com.zyf.runtime.support.redis


import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.base.SystemEnvironment
import com.zyf.common.constant.RedisKeyConst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

/**
 * redis 一顿操作
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/8/25 21:57
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Service
class RedisService (
    val objectMapper: ObjectMapper,
    val stringRedisTemplate: StringRedisTemplate,
    val redisTemplate: RedisTemplate<String?, Any>,
    val redisValueOperations: ValueOperations<String, String>,
    val redisHashOperations: HashOperations<String, String, Any>,
    val redisListOperations: ListOperations<String, Any>,
    val redisSetOperations: SetOperations<String, Any>,
    val systemEnvironment: SystemEnvironment,
){


    /**
     * 生成redis key
     * @param prefix
     * @param key
     * @return
     */
    fun generateRedisKey(prefix: String, key: String): String {
        val currentEnvironment = systemEnvironment.currentEnvironment
        return systemEnvironment.projectName + RedisKeyConst.SEPARATOR + currentEnvironment.value + RedisKeyConst.SEPARATOR + prefix + key
    }

    fun getLock(key: String, expire: Long): Boolean {
        return redisValueOperations.setIfAbsent(key, System.currentTimeMillis().toString(), expire, TimeUnit.MILLISECONDS)!!
    }

    fun unLock(key: String) {
        redisValueOperations.operations.delete(key)
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    fun expire(key: String?, time: Long): Boolean {
        return redisTemplate.expire(key!!, time, TimeUnit.SECONDS)
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    fun getExpire(key: String?): Long {
        return redisTemplate.getExpire(key!!, TimeUnit.SECONDS)
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    fun hasKey(key: String?): Boolean {
        return redisTemplate.hasKey(key!!)
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    fun delete(vararg key: String?) {
        if (key.isNotEmpty()) {
            if (key.size == 1) {
                redisTemplate.delete(key[0]!!)
            } else {
                redisTemplate.delete((CollectionUtils.arrayToList(key) as Collection<String?>))
            }
        }
    }

    /**
     * 删除缓存
     *
     * @param keyList
     */
    fun delete(keyList: List<String?>?) {
        if (CollectionUtils.isEmpty(keyList)) {
            return
        }
        redisTemplate.delete(keyList!!)
    }

    //============================String=============================
    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    fun get(key: String?): String? {
        return if (key == null) null else redisValueOperations[key]
    }

    fun <T> getObject(key: String?, clazz: Class<T>?): T? {
        val json = this.get(key) ?: return null
        val obj: T = objectMapper.readValue(json, clazz)
        return obj
    }


    /**
     * 普通缓存放入
     */
    fun set(key: String, value: String) {
        redisValueOperations[key] = value
    }

    fun set(key: Any, value: Any?) {
        val jsonString: String = objectMapper.writeValueAsString(value)
        redisValueOperations[key.toString()] = jsonString
    }

    /**
     * 普通缓存放入
     */
    fun set(key: String, value: String, second: Long) {
        redisValueOperations[key, value, second] = TimeUnit.SECONDS
    }

    /**
     * 普通缓存放入并设置时间
     */
    fun set(key: Any, value: Any?, second: Long) {
        val jsonString: String = objectMapper.writeValueAsString(value)
        if (second > 0) {
            redisValueOperations[key.toString(), jsonString, second] = TimeUnit.SECONDS
        } else {
            set(key.toString(), jsonString)
        }
    }


    //============================ map =============================
    fun mset(key: String, hashKey: String, value: Any) {
        redisHashOperations.put(key, hashKey, value)
    }

    fun mget(key: String, hashKey: String?): Any? {
        return redisHashOperations[key, hashKey!!]
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RedisService::class.java)

        /**
         * redis key 解析成真实的内容
         * @param redisKey
         * @return
         */
        fun redisKeyParse(redisKey: String): String {
            if (StrUtil.isBlank(redisKey)) {
                return ""
            }
            val index = redisKey.lastIndexOf(RedisKeyConst.SEPARATOR)
            if (index < 1) {
                return redisKey
            }
            return redisKey.substring(index)
        }

        /**
         * 获取当天剩余的秒数
         *
         * @return
         */
        fun currentDaySecond(): Long {
            return ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
        }
    }
}