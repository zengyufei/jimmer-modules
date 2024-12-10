package com.zyf.goods.service

import com.alibaba.excel.EasyExcel
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.CategoryTypeEnum
import com.zyf.common.enums.DataTracerTypeEnum
import com.zyf.common.enums.GoodsStatusEnum
import com.zyf.common.exception.BusinessException
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.goods.Category
import com.zyf.goods.Goods
import com.zyf.goods.goodsId
import com.zyf.service.dto.*
import com.zyf.support.service.DataTracerService
import com.zyf.support.service.DictCacheService
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Slf4j
@Service
class GoodsService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    private val categoryService: CategoryService,
    private val dataTracerService: DataTracerService,
    private val dictCacheService: DictCacheService
) {

    /**
     * 添加商品
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(addForm: GoodsAddForm): ResponseDTO<String?> {
        val result = sql.insert(addForm)
        dataTracerService.insert(result.modifiedEntity.goodsId, DataTracerTypeEnum.GOODS)
        return ResponseDTO.ok()
    }

    /**
     * 更新商品
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(updateForm: GoodsUpdateForm): ResponseDTO<String?> {
        // 商品校验
        updateForm.categoryId?.let {
            val res = checkGoods(it)
            if (!res.ok) {
                return res
            }
        }

        sql.update(updateForm)
//        val originEntity = updateForm.goodsId?.let { sql.findById(Goods::class, it) }
//        dataTracerService.update(updateForm.goodsId, DataTracerTypeEnum.GOODS, originEntity, goods)
        return ResponseDTO.ok()
    }

    /**
     * 添加/更新 商品校验
     */
    private fun checkGoods(categoryId: String): ResponseDTO<String?> {
        val category = sql.findById(Category::class, categoryId)
        if (category == null || !CategoryTypeEnum.GOODS.equalsValue(category.categoryType)) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST, "商品类目不存在~")
        }
        return ResponseDTO.ok()
    }


    /**
     * 删除
     */
    @Transactional(rollbackFor = [Exception::class])
    fun delete(goodsId: String): ResponseDTO<String?> {
        val goods = sql.findById(Goods::class, goodsId)
            ?: return ResponseDTO.userErrorParam("商品不存在")

        if (goods.goodsStatus != GoodsStatusEnum.SELL_OUT) {
            return ResponseDTO.userErrorParam("只有售罄的商品才可以删除")
        }

        batchDelete(listOf(goodsId))

        dataTracerService.batchDelete(listOf(goodsId), DataTracerTypeEnum.GOODS)
        return ResponseDTO.ok()
    }


    /**
     * 批量删除
     */
    fun batchDelete(goodsIdList: List<String>): ResponseDTO<String?> {
        if (goodsIdList.isEmpty()) {
            return ResponseDTO.ok()
        }

        sql.deleteByIds(Goods::class, goodsIdList)
        return ResponseDTO.ok()
    }

    /**
     * 分页查询
     */
    fun query(
        pageBean: PageBean,
        queryForm: GoodsQueryForm
    ): ResponseDTO<PageResult<GoodsVO>> {
        val pageResult = sql.createQuery(Goods::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.goodsId.desc())

            where(queryForm)
            select(table.fetch(GoodsVO::class))
        }.page(pageBean)

        if (pageResult.emptyFlag) {
            return ResponseDTO.ok(pageResult)
        }
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 商品导入
     */
    fun importGoods(file: MultipartFile): ResponseDTO<String?> {
        val dataList = try {
            val doReadSync = EasyExcel.read(file.inputStream)
                .head(GoodsImportForm::class.java)
                .sheet()
                .doReadSync<Any>()
            doReadSync
        } catch (e: IOException) {
            log.error(e.message, e)
            throw BusinessException("数据格式存在问题，无法读取")
        }

        if (dataList.isEmpty()) {
            return ResponseDTO.userErrorParam("数据为空")
        }

        return ResponseDTO.okMsg("成功导入${dataList.size}条，具体数据为：${objectMapper.writeValueAsString(dataList)}")
    }

    /**
     * 商品导出
     */
    fun getAllGoods(): List<GoodsExcelVO> {
        return sql.findAll(GoodsVO::class).map { e ->
            GoodsExcelVO(e.toEntity())
        }
    }
}