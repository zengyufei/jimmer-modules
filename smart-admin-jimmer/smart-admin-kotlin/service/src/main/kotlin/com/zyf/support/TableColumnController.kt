package com.zyf.support

import com.zyf.common.domain.ResponseDTO
import com.zyf.login.domain.RequestEmployee
import com.zyf.support.domain.TableColumnUpdateForm
import com.zyf.support.service.TableColumnService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("TableColumn Api")
@RestController
class TableColumnController(
    val sqlClient: KSqlClient,
    val tableColumnService: TableColumnService
) {

    /** 修改表格列 */
    @Api
    @PostMapping("/support/tableColumn/update")
    fun updateTableColumn(@RequestBody @Valid updateForm: TableColumnUpdateForm): ResponseDTO<String?> {
        val requestUser = RequestEmployee()
        requestUser.userId = "1"
        tableColumnService.updateTableColumns(requestUser, updateForm)
        return ResponseDTO.ok()
    }

    /** 恢复默认（删除） */
    @Api
    @GetMapping("/support/tableColumn/delete/{tableId}")
    fun deleteTableColumn(@PathVariable tableId: String): ResponseDTO<String?> {
        val requestUser = RequestEmployee()
        requestUser.userId = "1"
        tableColumnService.deleteTableColumn(requestUser, tableId)
        return ResponseDTO.ok()
    }

    /** 查询表格列 */
    @Api
    @GetMapping("/support/tableColumn/getColumns/{tableId}")
    fun getColumns(@PathVariable tableId: String): ResponseDTO<String?> {
        val requestUser = RequestEmployee()
        requestUser.userId = "1"
        return ResponseDTO.ok(tableColumnService.getTableColumns(requestUser, tableId))
    }
}