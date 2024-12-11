package com.zyf.support

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.ReloadItemVO
import com.zyf.service.dto.ReloadResultVO
import com.zyf.support.domain.ReloadForm
import com.zyf.support.service.ReloadService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * reload (内存热加载、钩子等)
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@RestController
@Tag(name = SwaggerTagConst.Support.RELOAD)
class ReloadController(
   val reloadService: ReloadService
) {

    @Operation(summary = "查询reload列表 @author 开云")
    @GetMapping("/support/reload/query")
    fun query(): ResponseDTO<List<ReloadItemVO>> {
        return reloadService.query()
    }

    @Operation(summary = "获取reload result @author 开云")
    @GetMapping("/support/reload/result/{tag}")
    @SaCheckPermission("support:reload:result")
    fun queryReloadResult(@PathVariable("tag") tag: String?): ResponseDTO<List<ReloadResultVO>> {
        return reloadService.queryReloadItemResult(tag)
    }

    @Operation(summary = "通过tag更新标识 @author 开云")
    @PostMapping("/support/reload/update")
    @SaCheckPermission("support:reload:update")
    fun updateByTag(@RequestBody reloadForm: @Valid ReloadForm): ResponseDTO<String?> {
        return reloadService.updateByTag(reloadForm)
    }
}
