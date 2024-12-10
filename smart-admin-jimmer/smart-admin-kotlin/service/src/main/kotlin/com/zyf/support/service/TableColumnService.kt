package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.RequestUser
import com.zyf.common.enums.UserTypeEnum
import com.zyf.support.TableColumn
import com.zyf.support.columns
import com.zyf.support.domain.TableColumnUpdateForm
import com.zyf.support.tableId
import com.zyf.support.userId
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service

@Slf4j
@Service
class TableColumnService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 获取 - 表格列
     */
    fun getTableColumns(requestUser: RequestUser, tableId: String): String? {
        return sql.createQuery(TableColumn::class) {
            where(table.tableId eq tableId)
            where(table.userId eq requestUser.userId)
            select(table.columns)
        }
            .fetchOneOrNull()
    }

    /**
     * 更新表格列
     */
    fun updateTableColumns(requestUser: RequestUser, updateForm: TableColumnUpdateForm) {
        if (updateForm.columnList.isNullOrEmpty()) {
            return
        }

        val tableId = updateForm.tableId
        val oldTableColumn = getTableColumns(requestUser, tableId)

        if (oldTableColumn == null) {
            val tableColumn = TableColumn {
                this.tableId = tableId
                this.userId = requestUser.userId!!
                this.userType = requestUser.userType?.value ?: UserTypeEnum.ADMIN_EMPLOYEE.value
                this.columns = objectMapper.writeValueAsString(updateForm.columnList)
            }
            sql.insert(tableColumn)
        } else {
            val tableColumn = TableColumn {
                this.tableId = tableId
                this.columns = objectMapper.writeValueAsString(updateForm.columnList)
            }
            sql.update(tableColumn)
        }
    }

    /**
     * 删除表格列
     */
    fun deleteTableColumn(requestUser: RequestUser, tableId: String) {
        sql.createDelete(TableColumn::class) {
            where(table.tableId eq tableId)
            where(table.userId eq requestUser.userId)
        }.execute()
    }
}