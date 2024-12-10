package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.service.dto.OperateLogQueryForm
import com.zyf.service.dto.OperateLogVO
import com.zyf.support.service.OperateLogService
import org.springframework.web.bind.annotation.*

/**
 * 操作日志
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021-12-08 20:48:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
class OperateLogController(
    val operateLogService: OperateLogService
) {

    /** 分页查询 @author 罗伊 */
    @PostMapping("/support/operateLog/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody queryForm: OperateLogQueryForm
    ): ResponseDTO<PageResult<OperateLogVO>> {
        return operateLogService.queryByPage(pageBean, queryForm)
    }

    /** 详情 @author 罗伊 */
    @GetMapping("/support/operateLog/detail/{operateLogId}")
    fun detail(@PathVariable operateLogId: String): ResponseDTO<OperateLogVO?> {
        return operateLogService.detail(operateLogId)
    }

    /** 分页查询当前登录人信息 @author 善逸 */
    @PostMapping("/support/operateLog/page/query/login")
    fun queryByPageLogin(
        @Body pageBean: PageBean,
        @RequestBody queryForm: OperateLogQueryForm
    ): ResponseDTO<PageResult<OperateLogVO>> {
        val requestUser = SmartRequestUtil.requestUser
        return operateLogService.queryByPage(
            pageBean, queryForm.copy(
                operateUserId = requestUser?.userId,
                operateUserType = requestUser?.userType
            )
        )
    }
}
