package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.service.dto.LoginLogQueryForm
import com.zyf.service.dto.LoginLogVO
import com.zyf.support.service.LoginLogService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginLogController(
    private val loginLogService: LoginLogService
) {

    /** 分页查询 @author 卓大 */
    @Operation(summary = "分页查询 @author 卓大")
    @PostMapping("/support/loginLog/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody queryForm: LoginLogQueryForm
    ): ResponseDTO<PageResult<LoginLogVO>> {
        return loginLogService.queryByPage(pageBean, queryForm)
    }

    /** 分页查询当前登录人信息 @author 善逸 */
    @Operation(summary = "分页查询当前登录人信息 @author 善逸")
    @PostMapping("/support/loginLog/page/query/login")
    fun queryByPageLogin(
        @Body pageBean: PageBean,
        @RequestBody queryForm: LoginLogQueryForm
    ): ResponseDTO<PageResult<LoginLogVO>> {
        val requestUser = SmartRequestUtil.requestUser
        return loginLogService.queryByPage(
            pageBean, queryForm.copy(
                userId = requestUser?.userId,
                userType = requestUser?.userType?.value
            )
        )
    }
}