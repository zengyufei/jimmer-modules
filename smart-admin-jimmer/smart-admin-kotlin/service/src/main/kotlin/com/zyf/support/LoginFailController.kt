package com.zyf.support;

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.support.service.LoginFailService
import com.zyf.service.dto.LoginFailQueryForm
import com.zyf.service.dto.LoginFailVO
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*


/**
 * 登录失败次数记录表(LoginFail)表控制层
 *
 * @author makejava
 * @since 2024-12-09 16:44:35
 */
@Api("LoginFail Api")
@RestController
@OperateLog
@Tag(name = SwaggerTagConst.Support.PROTECT)
class LoginFailController(
    val sql: KSqlClient,
    val loginFailService: LoginFailService
) {


    /** 分页查询登录失败次数记录表模块 @author makejava */
    @Operation(summary = "分页查询登录失败次数记录表模块 @author makejava")
    @PostMapping("/support/protect/loginFail/queryPage")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: LoginFailQueryForm
    ): ResponseDTO<PageResult<LoginFailVO>> {
        return loginFailService.queryByPage(pageBean, queryForm)
    }

    /** 批量删除 @author 1024创新实验室-主任-卓大 */
    @Operation(summary = "批量删除 @author 1024创新实验室-主任-卓大")
    @GetMapping("/support/protect/loginFail/batchDelete")
    fun batchDelete(@RequestBody loginFailIds: List<String>): ResponseDTO<String?> {
        return loginFailService.batchDelete(loginFailIds)
    }


}

