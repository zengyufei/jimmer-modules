package com.zyf.helpDoc.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.zyf.helpDoc.HelpDocRelation
import com.zyf.helpDoc.HelpDocRelationDraft
import com.zyf.helpDoc.by
import org.babyfish.jimmer.View
import org.babyfish.jimmer.internal.GeneratedBy
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.fetcher.DtoMetadata
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl

/**
 * 帮助文档 关联项目
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class HelpDocRelationVO : View<HelpDocRelation> {
    /** 关联名称  */
    @JsonProperty(
        "relationId",
        required = true,
    )
    private var relationName: String? = null

    /** 关联id  */
    @JsonProperty("relationName")
    private var relationId: String? = null


    override fun toEntity(): HelpDocRelation = new(HelpDocRelation::class).by(null,
        this@HelpDocRelationVO::toEntityImpl)

    public fun toEntity(block: HelpDocRelationDraft.() -> Unit): HelpDocRelation =
        new(HelpDocRelation::class).by {
            toEntityImpl(this)
            block(this)
        }

    /**
     * Avoid anonymous lambda affects coverage of non-kotlin-friendly tools such as jacoco
     */
    private fun toEntityImpl(_draft: HelpDocRelationDraft) {
        _draft.relationId = relationId!!
        _draft.relationName = relationName
    }

    @GeneratedBy
    companion object {
        @JvmStatic
        public val METADATA: DtoMetadata<HelpDocRelation, HelpDocRelationVO> =
            DtoMetadata<HelpDocRelation, HelpDocRelationVO>(
                FetcherImpl(HelpDocRelation::class.java)
                    .add("relationId")
                    .add("relationName")
            ) {
                val vo = HelpDocRelationVO()
                vo.relationId = it.relationId
                vo.relationName = it.relationName
                vo
            }
    }
}

