package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.*
import com.zyf.support.service.DictCacheService
import com.zyf.support.service.DictService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("Dict Api")
@RestController
class DictController(
    val sqlClient: KSqlClient,
    val dictService: DictService,
    val dictCacheService: DictCacheService
) {

    /** 分页查询数据字典KEY - @author 罗伊 */
    @PostMapping("/support/dict/key/query")
    fun keyQuery(
        @Body pageBean: PageBean,
        @Valid @RequestBody queryForm: DictKeyQueryForm
    ): ResponseDTO<PageResult<DictKeyVO>> {
        return ResponseDTO.ok(dictService.keyQuery(pageBean, queryForm))
    }

    /** 数据字典KEY-添加- @author 罗伊 */
    @PostMapping("/support/dict/key/add")
    fun keyAdd(@Valid @RequestBody keyAddForm: DictKeyAddForm): ResponseDTO<String?> {
        val errorCode = dictService.keyAdd(keyAddForm)
        return errorCode?.let {
            ResponseDTO.error(errorCode)
        } ?: run {
            ResponseDTO.ok()
        }
    }

    /** 数据字典缓存-刷新- @author 罗伊 */
    @GetMapping("/support/dict/cache/refresh")
    fun cacheRefresh(): ResponseDTO<String?> {
        dictCacheService.cacheRefresh()
        return ResponseDTO.ok()
    }

    /** 数据字典Value-添加- @author 罗伊 */
    @PostMapping("/support/dict/value/add")
    fun valueAdd(@Valid @RequestBody valueAddForm: DictValueAddForm): ResponseDTO<String?> {
        val errorCode = dictService.valueAdd(valueAddForm)
        return errorCode?.let {
            ResponseDTO.error(errorCode)
        } ?: run {
            ResponseDTO.ok()
        }
    }

    /** 数据字典KEY-更新- @author 罗伊 */
    @PostMapping("/support/dict/key/edit")
    fun keyEdit(@Valid @RequestBody keyUpdateForm: DictKeyUpdateForm): ResponseDTO<String?> {
        val errorCode = dictService.keyEdit(keyUpdateForm)
        return errorCode?.let {
            ResponseDTO.error(errorCode)
        } ?: run {
            ResponseDTO.ok()
        }
    }

    /** 数据字典Value-更新- @author 罗伊 */
    @PostMapping("/support/dict/value/edit")
    fun valueEdit(@Valid @RequestBody valueUpdateForm: DictValueUpdateForm): ResponseDTO<String?> {
        val errorCode = dictService.valueEdit(valueUpdateForm)
        return errorCode?.let {
            ResponseDTO.error(errorCode)
        } ?: run {
            ResponseDTO.ok()
        }
    }

    /** 数据字典KEY-删除- @author 罗伊 */
    @PostMapping("/support/dict/key/delete")
    fun keyDelete(@RequestBody keyIdList: List<String>): ResponseDTO<String?> {
        dictService.keyDelete(keyIdList)
        return ResponseDTO.ok()
    }

    /** 数据字典Value-删除- @author 罗伊 */
    @PostMapping("/support/dict/value/delete")
    fun valueDelete(@RequestBody valueIdList: List<String>): ResponseDTO<String?> {
        dictService.valueDelete(valueIdList)
        return ResponseDTO.ok()
    }


    /** 查询全部字典key - @author 卓大 */
    @GetMapping("/support/dict/key/queryAll")
    fun queryAll(): ResponseDTO<List<DictKeyVO>> {
        return ResponseDTO.ok(dictService.queryAllKey())
    }

    /** 分页查询数据字典value - @author 罗伊 */
    @PostMapping("/support/dict/value/query")
    fun valueQuery(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: DictValueQueryForm
    ): ResponseDTO<PageResult<DictValueVO>> {
        return ResponseDTO.ok(dictService.valueQuery(pageBean, queryForm))
    }

    /** 数据字典-值列表- @author 罗伊 */
    @GetMapping("/support/dict/value/list/{keyCode}")
    fun valueList(@PathVariable keyCode: String): ResponseDTO<List<DictValueVO>> {
        val dictValueVOList = dictCacheService.selectByKeyCode(keyCode)
        return ResponseDTO.ok(dictValueVOList)
    }
}