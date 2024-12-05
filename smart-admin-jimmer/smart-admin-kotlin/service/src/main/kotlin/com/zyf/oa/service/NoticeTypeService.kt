package com.zyf.oa.service

import cn.hutool.core.util.StrUtil
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.oa.NoticeType
import com.zyf.oa.by
import com.zyf.oa.noticeTypeId
import com.zyf.oa.noticeTypeName
import com.zyf.service.dto.NoticeTypeVO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.exists
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 通知。公告 类型
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
class NoticeTypeService(
    val sql: KSqlClient
) {

    /**
     * 查询全部
     */
    fun getAll(): List<NoticeTypeVO> {
        return sql.findAll(NoticeTypeVO::class)
    }

    fun getByNoticeTypeId(noticceTypeId: String): NoticeTypeVO? {
        return sql.findById(NoticeTypeVO::class, noticceTypeId)
    }

    @Transactional
    fun add(name: String?): ResponseDTO<String?> {
        name?.takeIf { it.isNotBlank() } ?: return ResponseDTO.userErrorParam("类型名称不能为空")

        if (sql.exists(NoticeType::class) {
                where(table.noticeTypeName eq name)
            }) {
            return ResponseDTO.userErrorParam("类型名称已经存在")
        }

        sql.insert(NoticeType {
            this.noticeTypeName = name
        })
        return ResponseDTO.ok()
    }

    @Transactional
    fun update(noticeTypeId: String, name: String?): ResponseDTO<String?> {
        name?.takeIf { it.isNotBlank() } ?: return ResponseDTO.userErrorParam("类型名称不能为空")

//        sql.findById(NoticeType::class, noticeTypeId)
//            ?: return ResponseDTO.userErrorParam("类型名称不存在")

        if (sql.exists(NoticeType::class) {
                where(table.noticeTypeName ne name)
                where(table.noticeTypeId ne noticeTypeId)
            }) {
            return ResponseDTO.userErrorParam("类型名称已经存在")
        }

        sql.update(NoticeType {
            this.noticeTypeId = noticeTypeId
            this.noticeTypeName = name
        })
        return ResponseDTO.ok()
    }

    @Transactional
    fun delete(noticeTypeId: String): ResponseDTO<String?> {
        sql.deleteById(NoticeType::class, noticeTypeId)
        return ResponseDTO.ok()
    }
} 