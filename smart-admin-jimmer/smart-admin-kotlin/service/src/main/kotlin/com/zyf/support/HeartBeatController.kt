package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.HeartBeatRecordQueryForm
import com.zyf.service.dto.HeartBeatRecordVO
import com.zyf.support.heartbeat.HeartBeatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 心跳记录
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Tag(name = SwaggerTagConst.Support.HEART_BEAT)
@RestController
class HeartBeatController(
    val heartBeatService: HeartBeatService
) {

    @PostMapping("/support/heartBeat/query")
    @Operation(summary = "查询心跳记录 @author 卓大")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody pageParam: @Valid HeartBeatRecordQueryForm
    ): ResponseDTO<PageResult<HeartBeatRecordVO>> {
        return heartBeatService.pageQuery(pageBean, pageParam)
    }
}
