package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.DataTracerQueryForm
import com.zyf.service.dto.DataTracerVO
import com.zyf.support.service.DataTracerService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 数据变动记录
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@RestController
class DataTracerController(
    val dataTracerService: DataTracerService,
) {

    /** 分页查询业务操作日志 - @author 卓大  */
    @Operation(summary = "分页查询业务操作日志 - @author 卓大 ")
    @PostMapping("/support/dataTracer/query")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid DataTracerQueryForm
    ): ResponseDTO<PageResult<DataTracerVO>> {
        return dataTracerService.query(pageBean, queryForm)
    }
}
