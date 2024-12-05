package com.zyf.helpDoc

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_help_doc_relation")
interface HelpDocRelation : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "relation_id")
    val relationId: String

    /** 关联名称 */
    @Column(name = "relation_name")
    val relationName: String?

    /** 文档id */
    @IdView("helpDoc")
    val helpDocId: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "help_doc_id")
    val helpDoc: HelpDoc?
}