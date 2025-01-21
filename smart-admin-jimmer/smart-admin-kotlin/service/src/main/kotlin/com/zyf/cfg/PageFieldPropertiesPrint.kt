package com.zyf.cfg

import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Slf4j
@Component
class PageFieldPropertiesPrint(

    @Value("\${project.pageField.enabled}")
    private val pageFieldEnabled: Boolean,
    @Value("\${project.pageField.pageNum}")
    private val pageFieldPageNum: String,
    @Value("\${project.pageField.pageSize}")
    private val pageFieldPageSize: String,
    @Value("\${project.pageField.sortCode}")
    private val pageFieldSortCode: String,
) : InitializingBean {

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (pageFieldEnabled) {
            log.info("自动识别分页参数功能开启！")
            log.info("当前页参数名：{}", pageFieldPageNum)
            log.info("分页大小参数名：{}", pageFieldPageSize)
            log.info("排序字段参数名：{}", pageFieldSortCode)
        } else {
            log.info("未开启自动识别分页参数功能。")
        }
    }
}