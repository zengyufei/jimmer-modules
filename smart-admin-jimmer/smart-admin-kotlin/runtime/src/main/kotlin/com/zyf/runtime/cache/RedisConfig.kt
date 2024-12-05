package com.zyf.runtime.cache

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.redisson.spring.starter.RedissonAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * redis配置
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Configuration
@ConditionalOnClass(RedissonAutoConfiguration::class)
class RedisConfig(
    val factory: RedisConnectionFactory
) {

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val om = ObjectMapper()
        om.registerModule(JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val jackson2JsonRedisSerializer: Jackson2JsonRedisSerializer<Any> = Jackson2JsonRedisSerializer(Any::class.java)
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        // enableDefaultTyping 官方已弃用 所以改为 activateDefaultTyping
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(factory!!)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashKeySerializer = jackson2JsonRedisSerializer
        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.setDefaultSerializer(StringRedisSerializer())
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun hashOperations(redisTemplate: RedisTemplate<String?, Any?>): HashOperations<String?, String, Any> {
        return redisTemplate.opsForHash()
    }

    @Bean
    fun valueOperations(redisTemplate: RedisTemplate<String?, String?>): ValueOperations<String?, String?> {
        return redisTemplate.opsForValue()
    }

    @Bean
    fun listOperations(redisTemplate: RedisTemplate<String?, Any?>): ListOperations<String?, Any?> {
        return redisTemplate.opsForList()
    }

    @Bean
    fun setOperations(redisTemplate: RedisTemplate<String?, Any?>): SetOperations<String?, Any?> {
        return redisTemplate.opsForSet()
    }

    @Bean
    fun zSetOperations(redisTemplate: RedisTemplate<String?, Any?>): ZSetOperations<String?, Any?> {
        return redisTemplate.opsForZSet()
    }
}
