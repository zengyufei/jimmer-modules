package com.zyf.repository.employee

import com.zyf.employee.Department
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

@Repository
class DepartmentRepository(
    sql: KSqlClient
) : BaseRepository<Department, String>(sql) {


}
