package com.zyf.goods

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.ResponseDTO
import com.zyf.goods.service.CategoryService
import com.zyf.service.dto.*
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * 类目
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2021/08/05 21:26:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
class CategoryController(
    private val categoryService: CategoryService
) {

    /** 添加类目 @author 胡克 */
    @Operation(summary = "添加类目 @author 胡克")
    @PostMapping("/category/add")
    @SaCheckPermission("category:add")
    fun add(@RequestBody @Valid addForm: CategoryAddForm): ResponseDTO<String?> {
        return categoryService.add(addForm)
    }

    /** 更新类目 @author 胡克 */
    @Operation(summary = "更新类目 @author 胡克")
    @PostMapping("/category/update")
    @SaCheckPermission("category:update")
    fun update(@RequestBody @Valid updateForm: CategoryUpdateForm): ResponseDTO<String?> {
        return categoryService.update(updateForm)
    }

    /** 查询类目详情 @author 胡克 */
    @Operation(summary = "查询类目详情 @author 胡克")
    @GetMapping("/category/{categoryId}")
    fun queryDetail(@PathVariable categoryId: String): ResponseDTO<CategoryVO?> {
        return categoryService.queryDetail(categoryId)
    }

    /** 查询类目层级树 @author 胡克 */
    @Operation(summary = "查询类目层级树 @author 胡克")
    @PostMapping("/category/tree")
    @SaCheckPermission("category:tree")
    fun queryTree(@RequestBody @Valid queryForm: CategoryTreeQueryForm): ResponseDTO<List<CategoryTreeVO>?> {
        return categoryService.queryTree(queryForm)
    }

    /** 删除类目 @author 胡克 */
    @Operation(summary = "删除类目 @author 胡克")
    @GetMapping("/category/delete/{categoryId}")
    @SaCheckPermission("category:delete")
    fun delete(@PathVariable categoryId: String): ResponseDTO<String?> {
        return categoryService.delete(categoryId)
    }
} 