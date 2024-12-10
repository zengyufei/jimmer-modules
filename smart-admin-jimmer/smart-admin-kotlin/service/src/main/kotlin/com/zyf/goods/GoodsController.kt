package com.zyf.goods

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.goods.service.GoodsService
import com.zyf.runtime.utils.SmartExcelUtil
import com.zyf.service.dto.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * 商品业务
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2021-10-25 20:26:54
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
class GoodsController(
    private val goodsService: GoodsService
) {

    /** 分页查询 @author 胡克 */
    @Operation(summary = "分页查询 @author 胡克")
    @PostMapping("/goods/query")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: GoodsQueryForm
    ): ResponseDTO<PageResult<GoodsVO>> {
        return goodsService.query(pageBean, queryForm)
    }

    /** 添加商品 @author 胡克 */
    @Operation(summary = "添加商品 @author 胡克")
    @PostMapping("/goods/add")
    fun add(@RequestBody @Valid addForm: GoodsAddForm): ResponseDTO<String?> {
        return goodsService.add(addForm)
    }

    /** 更新商品 @author 胡克 */
    @Operation(summary = "更新商品 @author 胡克")
    @PostMapping("/goods/update")
    fun update(@RequestBody @Valid updateForm: GoodsUpdateForm): ResponseDTO<String?> {
        return goodsService.update(updateForm)
    }

    /** 删除 @author 卓大 */
    @Operation(summary = "删除 @author 卓大")
    @GetMapping("/goods/delete/{goodsId}")
    fun delete(@PathVariable goodsId: String): ResponseDTO<String?> {
        return goodsService.delete(goodsId)
    }

    /** 批量 @author 卓大 */
    @Operation(summary = "批量 @author 卓大")
    @PostMapping("/goods/batchDelete")
    fun batchDelete(@RequestBody @Valid idList: List<String>): ResponseDTO<String?> {
        return goodsService.batchDelete(idList)
    }

    // --------------- 导出和导入 -------------------

    /** 导入 @author 卓大 */
    @Operation(summary = "导入 @author 卓大")
    @PostMapping("/goods/importGoods")
    fun importGoods(@RequestParam file: MultipartFile): ResponseDTO<String?> {
        return goodsService.importGoods(file)
    }

    /** 导出 @author 卓大 */
    @GetMapping("/goods/exportGoods")
    @Throws(IOException::class)
    fun exportGoods(response: HttpServletResponse) {
        val goodsList = goodsService.getAllGoods()
        SmartExcelUtil.exportExcel(response, "商品列表.xlsx", "商品", GoodsExcelVO::class.java, goodsList)
    }
} 