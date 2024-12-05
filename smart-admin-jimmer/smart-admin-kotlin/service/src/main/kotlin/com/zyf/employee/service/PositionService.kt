package com.zyf.employee.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.employee.Position
import com.zyf.employee.sort
import com.zyf.service.dto.PositionAddForm
import com.zyf.service.dto.PositionSpecification
import com.zyf.service.dto.PositionUpdateForm
import com.zyf.service.dto.PositionVO
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Slf4j
@Service
class PositionService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    fun <T : View<Position>> list(viewType: KClass<T>): MutableList<T> {
        return sql.createQuery(Position::class) {
            orderBy(table.sort.asc())
            select(
                table.fetch(viewType)
            )
        }.execute().toMutableList()
    }

    fun listAll(): MutableList<PositionVO> {
        return list(PositionVO::class)
    }

    fun queryPage(pageBean: PageBean, params: PositionSpecification): PageResult<PositionVO> {
        return sql.createQuery(Position::class) {
            orderBy(pageBean)
            where(params)
            select(
                table.fetch(PositionVO::class)
            )
        }.page(pageBean)
    }

    fun addPosition(createDTO: PositionAddForm): Position {
        val insert = sql.insert(createDTO)
        clearCache()
        return insert.originalEntity
    }

    fun updatePosition(updateDTO: PositionUpdateForm): Position {
        val update = sql.update(updateDTO)
        clearCache()
        return update.originalEntity
    }


    fun deletePosition(positionId: String): Boolean {
        sql.deleteById(Position::class, positionId)

        // 清除缓存
        this.clearCache()
        return true
    }

    fun deletePositions(positionIds: List<String>): Boolean {
        sql.deleteByIds(Position::class, positionIds)

        // 清除缓存
        this.clearCache()
        return true
    }

    /**
     * 单个删除
     */
    fun delete(positionId: String?): ResponseDTO<String?> {
        if (positionId == null) {
            return ResponseDTO.ok()
        }

        sql.deleteById(Position::class, positionId)
        return ResponseDTO.ok()
    }

    /**
     * 分页查询
     *
     * @return
     */
    fun queryList(): List<PositionVO> {
        return sql.findAll(PositionVO::class)
    }


    /**
     * 清除自身以及下级的id列表缓存
     */
    private fun clearCache() {
    }


}