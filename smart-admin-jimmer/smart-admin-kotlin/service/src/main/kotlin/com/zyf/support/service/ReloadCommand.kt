package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.repository.support.ReloadItemRepository
import com.zyf.repository.support.ReloadResultRepository
import com.zyf.runtime.config.AbstractSmartReloadCommand
import com.zyf.runtime.domain.SmartReloadItem
import com.zyf.runtime.domain.SmartReloadResult
import com.zyf.support.ReloadItem
import com.zyf.support.ReloadResult
import jakarta.annotation.Resource
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Component

/**
 * reload 操作
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Component
class ReloadCommand(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val reloadItemRepository: ReloadItemRepository,
    val reloadResultRepository: ReloadResultRepository,
) : AbstractSmartReloadCommand() {

    /**
     * 读取数据库中SmartReload项
     *
     * @return List<ReloadItem>
    </ReloadItem> */
    override fun readReloadItem(): List<SmartReloadItem>? {
        val reloadItemList: List<ReloadItem> = reloadItemRepository.listAll()
        return SmartBeanUtil.copyList(reloadItemList, SmartReloadItem::class.java)
    }


    /**
     * 保存reload结果
     *
     * @param smartReloadResult
     */
    override fun handleReloadResult(smartReloadResult: SmartReloadResult?) {
        smartReloadResult?.let { r->
            val reloadResult = ReloadResult {
                // tag = smartReloadResult?.tag
                args = r.args
                r.identification?.let { identification = it }
                result = r.result
                exception = r.exception
            }
            sql.insert(reloadResult)
        }
    }
}
