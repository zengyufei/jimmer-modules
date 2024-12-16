package com.zyf.helpDoc.domain

import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.helpDoc.*
import com.zyf.service.dto.HelpDocAddForm
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.babyfish.jimmer.Input
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.bouncycastle.asn1.x500.style.RFC4519Style.title

/**
 * 帮助文档 关联项目
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class HelpDocRelationForm: Input<HelpDocRelation> {

    /** 关联名称 */
    var relationName: @NotBlank(message = "关联名称不能为空") String? = null

    /** 关联id */
    var relationId: @NotNull(message = "关联id不能为空") String? = null


    override fun toEntity(): HelpDocRelation = new(HelpDocRelation::class).by(null,
        this@HelpDocRelationForm::toEntityImpl)

    public fun toEntity(block: HelpDocRelationDraft.() -> Unit): HelpDocRelation = new(HelpDocRelation::class).by {
        toEntityImpl(this)
        block(this)
    }

    private fun toEntityImpl(_draft: HelpDocRelationDraft) {
        _draft.relationId = this@HelpDocRelationForm.relationId!!
        _draft.relationName = this@HelpDocRelationForm.relationName
    }


}
