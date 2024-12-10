package com.zyf.repository.oa

import com.zyf.oa.Enterprise
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

@Repository
class EnterpriseRepository(
    sql: KSqlClient
) : BaseRepository<Enterprise, String>(sql) {


}
