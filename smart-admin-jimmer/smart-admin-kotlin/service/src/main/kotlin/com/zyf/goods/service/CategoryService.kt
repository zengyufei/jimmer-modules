package com.zyf.goods.service;

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.goods.*
import com.zyf.service.dto.*
import org.babyfish.jimmer.kt.unload
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.exists
import org.springframework.stereotype.Service

/**
 * 类目
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2021/08/05 21:26:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
class CategoryService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 添加类目
     */
    fun add(addForm: CategoryAddForm): ResponseDTO<String?> {
        // 校验类目
        val res = checkCategory(addForm.parentId, addForm.categoryType, null, addForm.categoryName)
        if (!res.ok) {
            return res
        }

        // 保存数据
        sql.insert(addForm.toEntity {
            this.sort = addForm.sort ?: 0
        })

        // 更新缓存
//        categoryCacheManager.removeCache()
        return ResponseDTO.ok()
    }

    /**
     * 更新类目
     */
    fun update(updateForm: CategoryUpdateForm): ResponseDTO<String?> {
        val responseDTO = checkCategory(updateForm.parentId, updateForm.categoryType, updateForm.categoryId, updateForm.categoryName)
        if (!responseDTO.ok) {
            return responseDTO
        }

        // 不更新类目类型和父类id
        sql.update(updateForm.toEntity {
            unload(this, Category::categoryType)
            unload(this, Category::parentId)
        })
//        categoryCacheManager.removeCache()
        return ResponseDTO.ok()
    }

    /**
     * 新增/更新 类目时的 校验
     */
    private fun checkCategory(
        parentId: String?,
        categoryType: Int,
        categoryId: String?,
        categoryName: String,
    ): ResponseDTO<String?> {
        // 校验父级是否存在

        parentId?.let {
            if (categoryId == parentId) {
                return ResponseDTO.userErrorParam("父级类目怎么和自己相同了")
            }

            val parent = sql.findById(Category::class, parentId)
            parent?.let {
                if (categoryType != parent.categoryType) {
                    return ResponseDTO.userErrorParam("与父级类目类型不一致")
                }
            }
        }

        // 校验同父类下 名称是否重复
        val exists = sql.createQuery(Category::class) {
            where(table.parentId eq parentId)
            where(table.categoryType eq categoryType)
            where(table.categoryId `ne?` categoryId)
            where(table.categoryName eq categoryName)
            select(table)
        }.exists()

        if (exists) {
            return ResponseDTO.userErrorParam("同级下已存在相同类目~")
        }
        return ResponseDTO.ok()
    }

    /**
     * 查询类目详情
     */
    fun queryDetail(categoryId: String): ResponseDTO<CategoryVO?> {
        return ResponseDTO.ok(sql.findById(CategoryVO::class, categoryId))
    }

    /**
     * 根据父级id查询所有子类返回层级树
     */
    fun queryTree(queryForm: CategoryTreeQueryForm): ResponseDTO<List<CategoryTreeVO>?> {
        if (queryForm.categoryType == null) {
            return ResponseDTO.userErrorParam("类目类型不能为空")
        }

        val treeList = sql.createQuery(Category::class) {
            orderBy(table.sort.asc())
            where(table.parentId eq queryForm.parentId)
            where(table.categoryType eq queryForm.categoryType)
            select(table.fetch(CategoryTreeVO::class))
        }.execute()
        return ResponseDTO.ok(treeList)
    }

    /**
     * 删除类目
     */
    fun delete(categoryId: String): ResponseDTO<String?> {
        if (!sql.exists(Category::class) {
                where(table.categoryId eq categoryId)
            }) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        }

        val fetchUnlimitedCount = sql.createQuery(Category::class) {
            where(table.parentId eq categoryId)
            select(count(table))
        }.fetchUnlimitedCount()
        if (fetchUnlimitedCount > 0) {
            return ResponseDTO.userErrorParam("请先删除子级类目")
        }

        sql.deleteById(Category::class, categoryId)

        // 更新缓存
//        categoryCacheManager.removeCache()
        return ResponseDTO.ok()
    }
}
