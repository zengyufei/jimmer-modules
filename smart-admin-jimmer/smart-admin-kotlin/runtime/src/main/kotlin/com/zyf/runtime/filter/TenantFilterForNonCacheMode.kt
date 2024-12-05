package com.zyf.runtime.filter

import com.zyf.common.base.TenantAware
import com.zyf.common.base.tenant
import com.zyf.common.base.tenantId
import com.zyf.runtime.TenantProvider
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.filter.KFilter
import org.babyfish.jimmer.sql.kt.filter.KFilterArgs
import org.springframework.stereotype.Component

/*
 * This bean is only be used when cache is NOT enabled.
 */
@Component
class TenantFilterForNonCacheMode(
    protected val tenantProvider: TenantProvider
) : KFilter<TenantAware> {

    override fun filter(args: KFilterArgs<TenantAware>) {
        tenantProvider.tenant?.let {
            args.apply {
                where(table.tenantId eq it)
            }
        }
    }
}
