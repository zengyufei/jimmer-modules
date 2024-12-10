package com.zyf.support.service

import com.google.common.collect.Lists
import com.zyf.common.annotations.SmartReload
import com.zyf.common.constant.ReloadConst
import jakarta.annotation.Resource
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.stereotype.Service
import java.util.stream.Collectors

/**
 * 缓存操作
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021/10/11 20:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Service
class CacheService(
    val caffeineCacheManager: CaffeineCacheManager
) {

    /**
     * 获取所有缓存名称
     *
     */
    fun cacheNames(): List<String> {
        return Lists.newArrayList(caffeineCacheManager.cacheNames)
    }

    /**
     * 某个缓存下的所有key
     *
     */
    fun cacheKey(cacheName: String?): List<String> {
        val cache = caffeineCacheManager.getCache(cacheName!!) as CaffeineCache? ?: return Lists.newArrayList()
        val cacheKey: Set<Any> = cache.nativeCache.asMap().keys
        return cacheKey.stream().map { e: Any -> e.toString() }.collect(Collectors.toList())
    }

    /**
     * 移除某个key
     *
     */
    fun removeCache(cacheName: String?) {
        val cache = caffeineCacheManager!!.getCache(cacheName!!) as CaffeineCache?
        cache?.clear()
    }

    @SmartReload(ReloadConst.CACHE_SERVICE)
    fun clearAllCache() {
        val cacheNames = caffeineCacheManager.cacheNames
        for (name in cacheNames) {
            val cache = caffeineCacheManager.getCache(name!!) as CaffeineCache?
            cache?.clear()
        }
    }
}
