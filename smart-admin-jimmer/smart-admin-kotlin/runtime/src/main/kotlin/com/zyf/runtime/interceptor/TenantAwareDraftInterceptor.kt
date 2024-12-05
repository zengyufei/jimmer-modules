package com.zyf.runtime.interceptor

import com.zyf.common.base.TenantAware
import com.zyf.common.base.TenantAwareDraft
import com.zyf.runtime.TenantProvider
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TenantAwareDraftInterceptor(
    private val tenantProvider: TenantProvider,
    @Value("\${project.default-tenant}") private val defaultTenant: String
) : DraftInterceptor<TenantAware, TenantAwareDraft> {

    override fun beforeSave(draft: TenantAwareDraft, original: TenantAware?) {
        if (!isLoaded(draft, TenantAware::tenantId)) {
            draft.tenantId = tenantProvider.tenant ?: defaultTenant
        }
    }


}
