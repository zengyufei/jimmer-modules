package com.zyf.support

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.support.service.CacheService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * 缓存
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021/10/11 20:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@RestController
@Tag(name = SwaggerTagConst.Support.CACHE)
class CacheController(
    val cacheService: CacheService
) {

    @Operation(summary = "获取所有缓存 @author 罗伊")
    @GetMapping("/support/cache/names")
    @SaCheckPermission("support:cache:keys")
    fun cacheNames(): ResponseDTO<List<String>> {
        return ResponseDTO.ok(cacheService.cacheNames())
    }

    @Operation(summary = "移除某个缓存 @author 罗伊")
    @GetMapping("/support/cache/remove/{cacheName}")
    @SaCheckPermission("support:cache:delete")
    fun removeCache(@PathVariable cacheName: String): ResponseDTO<String?> {
        cacheService.removeCache(cacheName)
        return ResponseDTO.ok()
    }

    @Operation(summary = "获取某个缓存的所有key @author 罗伊")
    @GetMapping("/support/cache/keys/{cacheName}")
    @SaCheckPermission("support:cache:keys")
    fun cacheKeys(@PathVariable cacheName: String): ResponseDTO<List<String>> {
        return ResponseDTO.ok(cacheService.cacheKey(cacheName))
    }
}
