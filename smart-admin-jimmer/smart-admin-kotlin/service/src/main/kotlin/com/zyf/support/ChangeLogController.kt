package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.ChangeLogQueryForm
import com.zyf.service.dto.ChangeLogVO
import com.zyf.support.service.ChangeLogService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * 系统更新日志 Controller
 *
 * @Author 卓大
 * @Date 2022-09-26 14:53:50
 * @Copyright 1024创新实验室
 */
@RestController
class ChangeLogController(
    val changeLogService: ChangeLogService
) {

    /** 分页查询 @author 卓大 */
    @PostMapping("/support/changeLog/queryPage")
    fun queryPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: ChangeLogQueryForm
    ): ResponseDTO<PageResult<ChangeLogVO>> {
        return ResponseDTO.ok(changeLogService.queryPage(pageBean, queryForm))
    }


    /** 变更内容详情 @author 卓大 */
    @GetMapping("/support/changeLog/getDetail/{changeLogId}")
    fun getDetail(@PathVariable changeLogId: String?): ResponseDTO<ChangeLogVO?> {
        return ResponseDTO.ok(changeLogService.getById(changeLogId))
    }
}