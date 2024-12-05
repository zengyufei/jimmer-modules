package com.zyf.employee

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.employee.service.PositionService
import com.zyf.service.dto.PositionAddForm
import com.zyf.service.dto.PositionSpecification
import com.zyf.service.dto.PositionUpdateForm
import com.zyf.service.dto.PositionVO
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("position Api")
@RestController
class PositionController(
    val sqlClient: KSqlClient,
    val positionService: PositionService
) {

//    /** 查询岗位列表 */
//    @Api
//    @GetMapping("/position/listAll")
//    fun listAll(): ResponseDTO<MutableList<PositionVO>> {
//        return ResponseDTO.ok(positionService.listAll())
//    }


    /** 分页查询 */
    @Api
    @PostMapping("/position/queryPage")
    fun queryPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid params: PositionSpecification
    ): ResponseDTO<PageResult<PositionVO>> {
        return ResponseDTO.ok(
            positionService.queryPage(
                pageBean,
                params
            )
        )
    }


    /** 添加岗位 */
    @PostMapping("/position/add")
//    @SaCheckPermission("system:position:add")
    fun addPosition(@RequestBody @Valid  createDTO: PositionAddForm): ResponseDTO<Position> {
        return ResponseDTO.ok(positionService.addPosition(createDTO))
    }

    /** 更新岗位 */
    @PostMapping("/position/update")
//    @SaCheckPermission("system:position:update")
    fun updatePosition(@RequestBody @Valid  updateDTO: PositionUpdateForm): ResponseDTO<Position> {
        return ResponseDTO.ok(positionService.updatePosition(updateDTO))
    }

    /** 删除岗位 */
    @PostMapping("/position/batchDelete")
//    @SaCheckPermission("system:position:delete")
    fun batchDelete(@RequestBody positionIds: List<String>): ResponseDTO<String?> {
        positionService.deletePositions(positionIds)
        return ResponseDTO.ok()
    }

      /** 单个删除 @author kaiyun */
    @GetMapping("/position/delete/{positionId}")
    fun batchDelete(@PathVariable positionId: String): ResponseDTO<String?> {
        return positionService.delete(positionId)
    }

    /** 不分页查询 @author kaiyun */
    @GetMapping("/position/queryList")
    fun queryList(): ResponseDTO<List<PositionVO>> {
        return ResponseDTO.ok(positionService.queryList())
    }
}