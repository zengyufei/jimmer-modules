package com.zyf.helpDoc

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_help_doc")
interface HelpDoc : TenantAware, BaseEntity {

    /** id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "help_doc_id")
    val helpDocId: String

    /** 标题 */
    @Column(name = "title")
    val title: String

    /** 文本内容 */
    @Column(name = "content_text")
    val contentText: String

    /** html内容 */
    @Column(name = "content_html")
    val contentHtml: String

    /** 附件 */
    @Column(name = "attachment")
    val attachment: String?

    /** 排序字段 */
    @Column(name = "sort")
    val sort: Int

    /** 页面浏览量，传说中的pv */
    @Column(name = "page_view_count")
    val pageViewCount: Int

    /** 用户浏览量，传说中的uv */
    @Column(name = "user_view_count")
    val userViewCount: Int

    /** 作者 */
    @Column(name = "author")
    val author: String?

    /** 目录id */
    @IdView("helpDocCatalog")
    val helpDocCatalogId: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "help_doc_catalog_id")
    val helpDocCatalog: HelpDocCatalog?

    @OneToMany(mappedBy = "helpDoc")
    val helpDocRelations : List<HelpDocRelation>
}