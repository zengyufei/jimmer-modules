package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.repository.support.ReloadItemRepository
import com.zyf.repository.support.ReloadResultRepository
import com.zyf.service.dto.ReloadItemVO
import com.zyf.service.dto.ReloadResultVO
import com.zyf.support.ReloadItem
import com.zyf.support.copy
import com.zyf.support.createTime
import com.zyf.support.domain.ReloadForm
import com.zyf.support.tag
import jakarta.annotation.Resource
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * reload (内存热加载、钩子等)
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Service
class ReloadService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val reloadItemRepository: ReloadItemRepository,
    val reloadResultRepository: ReloadResultRepository,
) {

    /**
     * 查询
     *
     * @return
     */
    fun query(): ResponseDTO<List<ReloadItemVO>> {
        val list: List<ReloadItemVO> = reloadItemRepository.listAll(ReloadItemVO::class)
        return ResponseDTO.ok(list)
    }

    fun queryReloadItemResult(tag: String?): ResponseDTO<List<ReloadResultVO>> {
        val reloadResultList: List<ReloadResultVO> = reloadResultRepository.listAll(ReloadResultVO::class) {
            orderBy(table.createTime.desc())
            where(table.tag `eq?` tag)
        }
        return ResponseDTO.ok(reloadResultList)
    }


    /**
     * 通过标签更新标识符
     *
     * @param reloadForm
     * @return
     */
    fun updateByTag(reloadForm: ReloadForm): ResponseDTO<String?> {
        val reloadItem: ReloadItem = reloadItemRepository.by { table.tag `eq?` reloadForm.tag } ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        sql.update( reloadItem.copy {
            reloadForm.identification?.let { identification = it }
            reloadForm.args?.let { args = it }
        })
        return ResponseDTO.ok()
    }
}
