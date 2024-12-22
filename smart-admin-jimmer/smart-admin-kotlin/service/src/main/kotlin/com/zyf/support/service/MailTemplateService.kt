package com.zyf.support.service;

import com.zyf.support.MailTemplate;
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.oa.*
import com.zyf.repository.support.MailTemplateRepository
import com.zyf.service.dto.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * (MailTemplate)表服务实现类
 *
 * @author makejava
 * @since 2024-12-21 16:56:19
 */
@Slf4j
@Service("mailTemplateService")
class MailTemplateService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val mailTemplateRepository: MailTemplateRepository,
) {


}
