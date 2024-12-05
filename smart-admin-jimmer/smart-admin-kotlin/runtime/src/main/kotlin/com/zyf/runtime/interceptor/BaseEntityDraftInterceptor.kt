package com.zyf.runtime.interceptor

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.BaseEntityDraft
import com.zyf.common.utils.SmartRequestUtil
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BaseEntityDraftInterceptor : DraftInterceptor<BaseEntity, BaseEntityDraft> {

    /*
     * In this simple example, `BaseEntity` has only two fields: `createdTime` and `modifiedTime`.
     *
     * In actual projects, you can add more fields, such as `creator` and `modifier`,
     * and you can use the information of the permission system to set them as the current user.
     *
     * Since `DraftInterceptor` itself is a spring object, you can use any business information
     * for draft filling. This is why jimmer uses Spring-managed `DraftInterceptor` instead of
     * simply using ORM to support default value.
     */

    override fun beforeSave(draft: BaseEntityDraft, original: BaseEntity?) {
        val requestUser = SmartRequestUtil.requestUser
        if (!isLoaded(draft, BaseEntity::updateTime)) {
            draft.updateTime = LocalDateTime.now()
        }
        if (!isLoaded(draft, BaseEntity::updateTime)) {
            draft.updateId = requestUser?.userId ?: "system"
        }
        // `original === null` means `INSERT`
        if (original === null) {
            if (!isLoaded(draft, BaseEntity::createTime)) {
                draft.createTime = LocalDateTime.now()
            }
            if (!isLoaded(draft, BaseEntity::createId)) {
                draft.createId = requestUser?.userId ?: "system"
            }
        }
    }
}
