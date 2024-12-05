package com.zyf.helpDoc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.helpDoc.HelpDoc
import com.zyf.helpDoc.helpDocRelations
import com.zyf.helpDoc.relationId
import com.zyf.service.dto.HelpDocVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service

@Slf4j
@Service
class HelpDocService(
    val sqlClient: KSqlClient,
    val objectMapper: ObjectMapper,
) {


    /**
     * 获取详情
     *
     * @param relationId
     * @return
     */
    fun queryHelpDocByRelationId(inputRelationId: String): List<HelpDocVo> {
        return sqlClient.createQuery(HelpDoc::class) {
            where += table.helpDocRelations {
                relationId eq inputRelationId
            }
            select(table.fetch(HelpDocVo::class))
        }.execute()
    }
}