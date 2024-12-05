package com.zyf.helpDoc

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_help_doc_catalog")
interface HelpDocCatalog : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "help_doc_catalog_id")
    val helpDocCatalogId: String

    /** 名称 */
    @Column(name = "name")
    val helpDocCatalogName: String

    /** 排序字段 */
    @Column(name = "sort")
    val sort: Int

    /** 父级id */
    @IdView("parent")
    val parentId: String?

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: HelpDocCatalog?
}