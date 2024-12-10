package com.zyf.repository.support;

import com.zyf.goods.Category
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * 登录失败次数记录表(LoginFail)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-09 16:44:42
 */
 @Repository
class LoginFailRepository(
    sql: KSqlClient
) : BaseRepository<Category, String>(sql) {


}

