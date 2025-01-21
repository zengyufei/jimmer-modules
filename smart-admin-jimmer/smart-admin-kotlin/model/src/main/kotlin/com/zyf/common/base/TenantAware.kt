package com.zyf.common.base

import com.zyf.system.Tenant
import org.babyfish.jimmer.sql.*

@MappedSuperclass
interface TenantAware {

    @IdView("tenant")
    val tenantId: String?

    @Key
    @ManyToOne(inputNotNull = true)
    @JoinColumn(name="tenant_id", foreignKeyType = ForeignKeyType.FAKE)
    val tenant: Tenant?

}