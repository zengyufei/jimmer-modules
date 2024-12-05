package com.zyf.department

import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.department.domain.DepartmentTreeVO
import com.zyf.department.service.DepartmentService
import com.zyf.employee.Department
import com.zyf.service.dto.DepartmentAddForm
import com.zyf.service.dto.DepartmentUpdateForm
import com.zyf.service.dto.DepartmentVO
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("department Api")
@RestController
class DepartmentController(
    val sqlClient: KSqlClient,
    val departmentService: DepartmentService
) {

    /** 查询部门列表 */
    @Api
    @GetMapping("/department/listAll")
    fun listAll(): ResponseDTO<MutableList<DepartmentVO>> {
        return ResponseDTO.ok(departmentService.listAll())
    }


    /** 查询部门树形列表 */
    @GetMapping("/department/treeList")
    fun departmentTree(): ResponseDTO<MutableList<DepartmentTreeVO>> {
        return ResponseDTO.ok(departmentService.departmentTree())
    }

    /** 分页查询 */
    @Api
    @PostMapping("/department/queryPage")
    fun queryPage(
        @RequestParam(defaultValue = "0") pageNum: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @RequestParam(defaultValue = "sort asc") sortCode: String
    ): ResponseDTO<PageResult<Department>> {
        return ResponseDTO.ok(
            departmentService.queryPage(
               PageBean.of(pageNum, pageSize, sortCode)
            )
        )
    }


    /** 添加部门 */
    @PostMapping("/department/add")
//    @SaCheckPermission("system:department:add")
    fun addDepartment(@RequestBody @Valid  createDTO: DepartmentAddForm): ResponseDTO<Department> {
        return ResponseDTO.ok(departmentService.addDepartment(createDTO))
    }

    /** 更新部门 */
    @PostMapping("/department/update")
//    @SaCheckPermission("system:department:update")
    fun updateDepartment(@RequestBody @Valid  updateDTO: DepartmentUpdateForm): ResponseDTO<Department> {
        return ResponseDTO.ok(departmentService.updateDepartment(updateDTO))
    }


    /** 删除部门 */
    @GetMapping("/department/delete/{departmentId}")
//    @SaCheckPermission("system:department:delete")
    fun deleteDepartment(@PathVariable departmentId: String): ResponseDTO<String?> {
        return ResponseDTO.ok(departmentService.deleteDepartment(departmentId))
    }
}