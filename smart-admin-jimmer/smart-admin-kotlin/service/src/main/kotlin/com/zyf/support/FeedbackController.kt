package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.service.dto.FeedbackAddForm
import com.zyf.service.dto.FeedbackQueryForm
import com.zyf.service.dto.FeedbackVO
import com.zyf.support.service.FeedbackService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 意见反馈
 *
 * @Author 1024创新实验室: 开云
 * @Date 2022-08-11 20:48:09
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
class FeedbackController(
    var feedbackService: FeedbackService
) {

    /** 意见反馈-分页查询 @author 开云 */
    @Operation(summary = "意见反馈-分页查询 @author 开云")
    @PostMapping("/support/feedback/query")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: FeedbackQueryForm
    ): ResponseDTO<PageResult<FeedbackVO>> {
        return feedbackService.query(pageBean, queryForm)
    }

    /** 意见反馈-新增 @author 开云 */
    @Operation(summary = "意见反馈-新增 @author 开云")
    @PostMapping("/support/feedback/add")
    fun add(@RequestBody @Valid addForm: FeedbackAddForm): ResponseDTO<String?> {
        val employee = SmartRequestUtil.requestUser
        return feedbackService.add(addForm, employee)
    }
} 